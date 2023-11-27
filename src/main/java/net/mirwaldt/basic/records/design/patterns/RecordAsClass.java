package net.mirwaldt.basic.records.design.patterns;

import java.util.Objects;

record PointAsRecord(int x, int y) {
};

public class RecordAsClass {
    private final int x, y;

    public RecordAsClass(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RecordAsClass)) return false;
        RecordAsClass other = (RecordAsClass) o;
        return other.x == x && other.y == y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("PointAsClass[x=%d, y=%d]", x, y);
    }
}