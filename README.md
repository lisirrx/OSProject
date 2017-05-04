# 操作系统进程管理3 红绿灯模拟

本项目模拟了十字路口受红绿灯调控时的车辆运行情况，可以添加特殊车辆无视红绿灯信号。

程序每隔3s会随机生成一些车辆。

点击不同车道旁的按钮可以向车道中添加车并观察运行情况。

点击随机生成可以添加车辆。

在出现死锁时会提示`Dead Lock！`， 点击清除死锁可以清除车道上的所有车。

![](https://github.com/lisirrx/OSProject/blob/master/screenshots/2.png)





**注意：** 由于程序是计算密集型，会在添加车辆后延迟变高，为了达到更好的模拟效果，整个程序会进行多线程的计算。使用线程数和CPU逻辑核心数挂钩，2核心以下使用单线程进行，4核心使用两个线程，4核心以上使用四个线程，8核心以上使用8个核心计算。

## 开发/运行环境

- `JDK1.8`
- `JavaFx2.0 `
- `开发环境：Windows10 Pro 1703`

## 项目结构

```json
src ---- crossing ---- fxml
                   |-- Model ---- CrossingMutex
                   |          |-- RGLight
                   |          |-- Road
                   |          |-- Vehicle
                   |          |-- Test
                   |
                   |-- util ---- TwoValueTuple
                   |          |-- Position
                   |          |-- CrossingMutexListener
                   |          |-- RGLightChangeListener
                   |
                   |---- Controller
                   |---- Main
                         
 
```



## 思路简介

首先，为了方便在不影响实际情况下模拟，做了车辆不会转向、倒车、变换车道的假设。

每条路双向四车道，每个方向上两个车道分别运行普通车辆（黑色）、特殊车辆（红色）。

红绿灯每3秒变色，当车运行到路口时会检查当下的红绿灯情况。

路口被车道划分为4*4的矩阵，每个方块是一个**互斥量**。



![](https://github.com/lisirrx/OSProject/blob/master/screenshots/1.jpg)



当有一辆车进入这个方块时，持有这个互斥量，离开时释放。

```java
public synchronized int checkAndSet(int x, int y) {
        int temp = mutex[x][y];
        if (temp == 1) {
            mutex[x][y]--;
        }
        return temp;
    }

public synchronized void release(int x, int y) {
        if (mutex[x][y] == 0) {
            mutex[x][y]++;
        }
    }
```



这里用每辆车**模拟**一个待执行的线程，事实上并没有为每个车创建一个线程。

整个项目通过对车辆的调度，完成了基本的进程调度思想的模拟，并练习了多线程编程的技能。

## 实现过程

### 界面

首先，界面采用了jdk中较Swing更新的GUI框架 `JavaFx` ， 使用`fxml` 文件定义布局，实现了逻辑和布局的分离。

在我的`layout.fxml`中，添加了车道、按钮、 红绿灯，并使用`fx:id`进行标记，这个写法让我觉得很像安卓开发时的感觉。

特别的，通过Java的反射机制，将所有负责添加车辆的按钮绑定到同一个函数上，获取按钮的id，并将按钮相关的资源进行序列化，大大减少了代码量。

### 系统层次

下面从`Main`类出发讲解一下大概的调用层次：



```java
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/layout.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setResizable(false);
        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setTitle("Crossing_Han Li v1.0");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
      
        Controller controller = fxmlLoader.getController();
        controller.init();

    }
```



可以看到，除了负责界面加载与初始化的代码，对于逻辑的控制只有 `controller`的 `init`方法。

我们的逻辑主要集中在`Controller` 类中，进行了各个对象的初始化和与界面元素的绑定。



```java
    public void init(){
        RGLight lightSN = new RGLight(RGLight.Direction.SN, Color.RED, NSLight);
        RGLight lightEW = new RGLight(RGLight.Direction.EW, Color.GREEN, EWLight);

        mutex = new CrossingMutex();
        crossingMutexListener = new CrossingMutexListener(mutex);
        listenerSN = new RGLightChangeListener(lightSN);
        listenerEW = new RGLightChangeListener(lightEW);

        lightSN.start();
        lightEW.start();
      
        cpuCount =  Runtime.getRuntime().availableProcessors();
        createThreadAndSetvelocity(cpuCount);
      
        }
    }
```



在`createThreadAndSetvelocity`里我们主要进行了各个事件监听的注册和线程的启动。其中包含大量进行线程数目限制的判断代码略显臃肿。以后考虑将其改为线程池方式组织。

那我们来看一下比较重要的`Road`类

```java
private CopyOnWriteArrayList<Vehicle> vehicles;

public void run() {
        for (Vehicle v : vehicles) {
            if (v.getMileage() >= 1000) {
                v.setMileage(Double.MAX_VALUE / 2);
            } else {
                if (v.getMileage() <= 1000) {
                    if (v.getMileage() > 800) {
                        v.setVisable(false);
                    }
                    v.move();
                }
            }
        }
}
```



截取了`Road` 类的主要部分，可以看到我使用了`java.util.concurrent`包中提供的`CopyOnWriteArrayList`。

这里主要是考虑到：

```java
//Controller.java
public void addAVehicle(int index) {
        Road current = roads[index];
        //````some code ````
        if (current.addVehicle(v)) {
        //````some code ````
        }
    }

//Road.java
public boolean addVehicle(Vehicle v){
        if ( vehicles.size() == 0 || vehicles.get(vehicles.size() - 1).getMileage() >= 70) {
            vehicles.add(v);
            v.setIndex(getCount() - 1);
            return true;
        }
        return false;
    }
  
```

用户按下按钮时，在主线程中，会对`Road`中存储`Vehicle`的集进行写入操作，考虑到线程安全的因素，没有使用

`ArrayList` ，当然，由于我用到了对数组的随机访问，所以没办法改成并发的LinkedList，而事实上，`CopyOnWriteArrayList`并不是很适合写入频繁的情况。

可以看到，我在添加`Vehicle`的时候判断了前一辆车的情况，所以**快速点击按钮是不会有错误情况的**。

然后看一下 `Vehicle`类的实现：

```java
    private RGLightChangeListener lightChangeListener;
    private CrossingMutexListener crossingMutexListener;
    private Road road;
    private int index;
    private Position position;
    private double velocity;
    private double mileage = 0;
    private SimpleBooleanProperty visable;

    public void move(){
        if (check()) {
          //move
        }
    }

    private boolean check(){
        double distance = 800;
        if (index != 0) {
 			//检查与前车距离
        }
        boolean flag = true;
        double judgeMileage = mileage；
        if (judgeMileage == Road.halfLength){
          //检查红绿灯和互斥量
        } else if (judgeMileage == Road.halfLength + Road.crossBlockLength){
           //检查互斥量
        } else if (judgeMileage == Road.halfLength + 2 * Road.crossBlockLength){
           //检查互斥量
        } else if (judgeMileage == Road.halfLength + 3 * Road.crossBlockLength){
           //检查互斥量
        } else if (judgeMileage == Road.halfLength + 4 * Road.crossBlockLength){
           //检查互斥量
        }
        return flag && distance >= safeDistance;
    }
		
	//检查互斥量
    private int checkMutexSignal(int index){
        return crossingMutexListener.checkSignal(road.getRowMutexIndexList()[index],
                road.getColMutexIndexList()[index]);
    }
    //释放
    private void releaseMutexSignal(int index){
        crossingMutexListener.release(road.getRowMutexIndexList()[index],
                road.getColMutexIndexList()[index]);
    }
```

在 `move`函数中，进行了是否可以前进的检查。每走到一个互斥量区域时，持有该互斥量，离开时释放。



### 事件的通知

由于每辆车并不是一个单独的线程，所以不能使用线程级的通知机制，所以这里经过各种实验之后，采取了一种类似**发布-订阅**模式的方法。命名采取了`xxListener` 的形式，可以看出，内部思维更像是同为**观察者模式**的事件监听机制，可是实现起来却很类似发布订阅，这是个值得深入研究的问题。



```java
public RGLightChangeListener(RGLight light){
   this.light = light;
}
public boolean getSignal(){
    return light.getColor();
}


//Vehicle.java
public void registRGLightChangeListener(RGLightChangeListener listener){
    this.lightChangeListener = listener;
}


```



可以看到，我们每个方向上的车道只有一个红绿灯，作为发布者，而订阅者通过这个监听器类监听红绿灯的变化。

这样我们可以完成跨线程的通讯。



### 死锁检查

首先，在`CrossingMutex`类中，使用dfs在互斥量区域中搜索是否成环，在`Controller`中进行计数，当发现始终无法解除死锁时会报警提示。

