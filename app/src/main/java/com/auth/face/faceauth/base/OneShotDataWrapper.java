package com.auth.face.faceauth.base;

public class OneShotDataWrapper<T> {

    public static <T> OneShotDataWrapper<T> of(T data) {
        return new OneShotDataWrapper<T>(data);
    }

    public boolean shot;
    public T data;

    private OneShotDataWrapper(T data) {
        this.data = data;
    }

    public void consume(OneShotDataWrapperConsumer<T> consumer) {
        if (shot) {
            return;
        }
        shot = true;
        consumer.consume(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof OneShotDataWrapper) {
            OneShotDataWrapper oneShot = (OneShotDataWrapper) o;
            return shot == oneShot.shot
                    && objectsEqual(data, oneShot.data);
        }
        return false;
    }

    private boolean objectsEqual(Object first, Object second) {
        return (first == null && second == null)
                || (first != null && first.equals(second));
    }

    @Override
    public String toString() {
        return "OneShotDataWrapper{" +
                "shot=" + shot +
                ", data=" + data +
                '}';
    }
}
