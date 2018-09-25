package karsai.laszlo.kasam.model;

public class Option {
    private float x;
    private float y;
    private String text;
    private int sequence;

    public Option(int sequence) {
        this.text = String.valueOf(sequence);
        this.sequence = sequence;
    }

    public String getText() {
        return text;
    }

}
