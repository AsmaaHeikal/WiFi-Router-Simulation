import java.util.ArrayList;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
class Semaphore {
    private int value;

    public Semaphore(int initial) {
        value = initial;
    }

    public synchronized void acquire(String name,String type) {
        value--;

        if (value < 0) {
            try {
                System.out.println("("+ name+") ("+type+") "+"Arrived and Waiting");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("("+ name+") ("+type+") "+"Arrived");
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
    private final Semaphore semaphore;
    int numberOfConnection;
    int maxConnections;
    ArrayList<Device> devices = new ArrayList<>();

    public Router(int maxConnections, ArrayList<Device> devices) {
        this.maxConnections = maxConnections;
        semaphore = new Semaphore(maxConnections);
        numberOfConnection = 0;
        this.devices = devices;
    }
    public void occupyConnection(String deviceName,String deviceType) throws InterruptedException{
        semaphore.acquire(deviceName,deviceType);
        synchronized (this) {
            numberOfConnection++;
            System.out.println("Connection " + numberOfConnection + " : " + deviceName + " Occupied");
        }
    }
    public void releaseConnection(String deviceName) throws InterruptedException{
        semaphore.release();
        synchronized (this) {
            System.out.println("Connection " + numberOfConnection + " : " + deviceName + " Logged out");
            numberOfConnection--;
        }
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
            //make occupyConnection called once the deviced started
//            router.occupyConnection(name,type);
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
        System.out.println("What is the number of devices Clients want to connect?");
        int TC = scanner.nextInt();
        Queue<Device> queue = new LinkedList<>();
        ArrayList<Device> devices = new ArrayList<>();
        Router router = new Router(N,devices);
        for (int i = 0; i < TC; i++) {
            String name = scanner.next();
            String type = scanner.next();
            devices.add(new Device(router,name, type));
        }
        for(int i=0;i<TC;i++){
            router.occupyConnection(devices.get(i).name(),devices.get(i).getType());
            devices.get(i).start();
        }
    }
}

