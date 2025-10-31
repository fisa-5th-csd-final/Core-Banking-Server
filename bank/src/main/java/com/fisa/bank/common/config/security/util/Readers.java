package com.fisa.bank.common.config.security.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 특정 경로에서, 문자열을 읽어오는 유틸 클래스
 */
public class Readers {

    public static String readFromFile(String filePath) {
        Path path = Paths.get(filePath);
        try(AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path)){
            ByteBuffer buffer = ByteBuffer.allocate(256);

            fileChannel.read(buffer, 0);
            return new String(buffer.asCharBuffer().array());
        } catch (IOException e){
            throw new IllegalStateException("Failed to read key file", e);
        }
    }
}
