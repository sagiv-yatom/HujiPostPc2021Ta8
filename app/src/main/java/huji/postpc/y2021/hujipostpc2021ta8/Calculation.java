package huji.postpc.y2021.hujipostpc2021ta8;

enum State {
    IN_PROGRESS,
    DONE,
    DELETED
}

public class Calculation {
    private String workerId;
    private State state;
    private long number;
    private long lastCalculation;
    private int progressPercent;
    private long root1;
    private long root2;

    public Calculation(long number) {
        this.number = number;
        this.state = State.IN_PROGRESS;
        this.lastCalculation = 2;
        this.progressPercent = 0;
    }

    public String getWorkerId() {
        return workerId;
    }

    public State getState() {
        return this.state;
    }

    public long getNumber() {
        return this.number;
    }

    public long getLastCalculation() {
        return this.lastCalculation;
    }

    public int getProgressPercent() {
        return this.progressPercent;
    }

    public long getRoot1() {
        return this.root1;
    }

    public long getRoot2() {
        return this.root2;
    }

    public void setWorkerId(String id) {
        this.workerId = id;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setLastCalculation(long calc) {
        this.lastCalculation = calc;
    }

    public void setProgressPercent(int progress) {
        this.progressPercent = progress;
    }

    public void setRoot1(long root) {
        this.root1 = root;
    }

    public void setRoot2(long root) {
        this.root2 = root;
    }

    public String serialize() {
        return workerId + "#" + state + "#" + number + "#" + lastCalculation +
                "#" + progressPercent + "#" + root1 + "#" + root2;
    }
}
