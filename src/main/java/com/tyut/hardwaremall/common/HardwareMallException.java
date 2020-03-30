package com.tyut.hardwaremall.common;

public class HardwareMallException extends RuntimeException {

    public HardwareMallException() {
    }

    public HardwareMallException(String message) {
        super(message);
    }

    /**
     * 丢出一个异常
     *
     * @param message
     */
    public static void fail(String message) {
        throw new HardwareMallException(message);
    }

}
