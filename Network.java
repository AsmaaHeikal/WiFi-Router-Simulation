import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
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
    boolean [] occupiedConnections; // if true then connection is occupied

    public Router(int maxConnections, ArrayList<Device> devices) {
        this.maxConnections = maxConnections;
        semaphore = new Semaphore(maxConnections);
        numberOfConnection = 0;
        this.devices = devices;
        occupiedConnections = new boolean[maxConnections];
    }
    public int occupyConnection(String deviceName,String deviceType) throws InterruptedException {
        semaphore.acquire(deviceName,deviceType);
        synchronized (this) {
            int i = 0;
            for (; i < maxConnections; i++) {
                if (!occupiedConnections[i]) {
                    occupiedConnections[i] = true;
                    break;
                }
            }
            numberOfConnection++;
            System.out.println("Connection " + (i+1) + " : " + deviceName + " Occupied");
            return i+1;
        }
    }
    public void releaseConnection(String deviceName, int index) throws InterruptedException {
        synchronized (this) {
            System.out.println("Connection " + (index) + " : " + deviceName + " Logged out");
            numberOfConnection--;
            occupiedConnections[index-1] = false; // no longer occupied
        }
        semaphore.release();
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
    private int connectionId;
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
            connectionId = router.occupyConnection(name,type);
            System.out.println("Connection " + connectionId + " : " + name + " Logged in\n"
                    + "Connection " + connectionId + " : " + name + " Performs online activity");
            sleepRandomTime();
            router.releaseConnection(name, connectionId);
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
        ArrayList<Device> devices = new ArrayList<>();
        Router router = new Router(N, devices);

        // Redirect System.out to a file named "log.txt"
        try {
            PrintStream fileOut = new PrintStream(new FileOutputStream("log.txt"));
            System.setOut(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < TC; i++) {
            String name = scanner.next();
            String type = scanner.next();
            devices.add(new Device(router, name, type));
        }
        for (int i = 0; i < TC; i++) {
            devices.get(i).start();
        }
    }
}

