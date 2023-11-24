import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
class Semaphore {
    private int value;

    public Semaphore(int initial) {
        value = initial;
    }

    public synchronized void acquire() {
        value--;

        if (value < 0) {
            try {
                System.out.println("Arrive and Waiting");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Arrived");
        }
    }

    public synchronized void release() {
        value++;

        if (value <= 0) {
            notify();
        }
    }
}

class Router {
    private final Semaphore semaphor;
    int numberOfConnection;
    int maxConnections;

    public Router(int maxConnections) {
        this.maxConnections = maxConnections;
        semaphor = new Semaphore(maxConnections);
        numberOfConnection = 0;
    }
    public void occupyConnection(String deviceName) throws InterruptedException{
        numberOfConnection++;
        semaphor.acquire();
//        //Connection 1: C1 Occupied
        System.out.println("Connection "+ numberOfConnection+" : " + deviceName + " Occupied");
    }
    public void releaseConnection(String deviceName) throws InterruptedException{
        numberOfConnection--;
        semaphor.release();
        System.out.println("Connection "+ numberOfConnection+" : " + deviceName + " Logged out");
    }
    public int getNumberOfConnection() {
        return numberOfConnection;
    }
    public int getMaxConnections() {
        return maxConnections;
    }
}

class Device extends Thread {
    private final Router router;
    private final String name;
    private final String type;

    public Device(Router router, String name, String type) {
        this.router = router;
        this.name = name;
        this.type = type;
    }
    public String getType() {
        return type;
    }
    public String name() {
        return name;
    }
    @Override
    public void run() {
        try {
            router.occupyConnection(name);
            System.out.println("Connection " + router.getNumberOfConnection() + " : " + name + " Logged in\n"
                    +"Connection " + router.getNumberOfConnection() + " : " + name + " Performs online activity");
            sleepRandomTime();
            router.releaseConnection(name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void sleepRandomTime() throws InterruptedException {
        Thread.sleep((long) (Math.random() * 2000));
    }
}

public class Network {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What is the number of WI-FI Connections?");
        int N = scanner.nextInt();
        Router router = new Router(N);
        System.out.println("What is the number of devices Clients want to connect?");
        int TC = scanner.nextInt();
        Queue<Device> queue = new LinkedList<>();
        Device [] devices = new Device[TC];
        for (int i = 0; i < TC; i++) {
            String name = scanner.next();
            String type = scanner.next();
            devices[i] = new Device(router,name, type);
        }
        for(int i=0;i<N;i++){
            devices[i].start();
        }
    }
}

