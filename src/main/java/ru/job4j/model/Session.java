package ru.job4j.model;

import java.util.Objects;

public class Session {
    private int id;
    private String name;
    private int numberOfRow;
    private int numberOfCell;

    public Session(int id, String name, int numberOfRow, int numberOfCell) {
        this.id = id;
        this.name = name;
        this.numberOfRow = numberOfRow;
        this.numberOfCell = numberOfCell;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfRow() {
        return numberOfRow;
    }

    public void setNumberOfRow(int numberOfRow) {
        this.numberOfRow = numberOfRow;
    }

    public int getNumberOfCell() {
        return numberOfCell;
    }

    public void setNumberOfCell(int numberOfCell) {
        this.numberOfCell = numberOfCell;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Session session = (Session) o;
        return id == session.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
