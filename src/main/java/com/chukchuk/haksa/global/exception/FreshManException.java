package com.chukchuk.haksa.global.exception;

public class FreshManException extends RuntimeException {
    public FreshManException(String message) {
        super(message); //신입생 구별을 위한 처리 로직 추가 필요
    }
}
