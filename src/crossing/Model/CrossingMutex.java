package crossing.Model;

import java.util.concurrent.locks.Lock;

/**
 * Created by lisirrx on 5/2/17.
 */
public class CrossingMutex {
    private volatile int[][] mutex = {{1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}};

    public int[][] getMutex() {
        return mutex;
    }

    public boolean checkDeadLock(){
        int [][]vis = new int[4][4];
        int []dx = {1,-1,0,0};
        int []dy = {0,0,1,-1};
        int cot;
        for(int i=0;i<4;i++) {
            for(int j=0;j<4;j++) {
                cot=0;
                if(checkCircle(vis, i, j, i, j, cot, dx, dy))
                    return true;
            }
        }
        return false;
    }

    private boolean checkCircle(int [][]vis, int sx, int sy, int x,int y, int cot, int [] dx, int [] dy){

            if(sx == x && sy == y && vis[sx][sy] == 1) {
                return cot >= 4;
            }
            vis[x][y]=1;
            for(int i=0;i<4;i++) {
                int tx=x+dx[i];
                int ty=y+dy[i];
                if(tx >= 0 && tx < 4 && ty >= 0 && ty < 4 && mutex[tx][ty] == 0
                        && vis[tx][ty] == 0 || (tx == sx && ty == sy)) {
                    cot++;
                    if(checkCircle(vis, sx, sy, tx,ty, cot, dx, dy))
                        return true;
                    cot--;
                }
            }
            return false;
        }

    public synchronized int checkAndSet(int x, int y) {
        int temp = mutex[x][y];
        if (temp == 1) {
            mutex[x][y]--;
        }
//        System.out.println(" Mutex : (" + x + ", " + y + ") " + temp);
        return temp;
    }

    public synchronized void release(int x, int y) {
        if (mutex[x][y] == 0) {
            mutex[x][y]++;
        }
    }
}
