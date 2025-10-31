package com.fisa.bank.common.config.security.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 특정 경로에서, 문자열을 읽어오는 유틸 클래스
 */
public class Readers {

    public static String readFromFile(String filePath) {
        Path path = Paths.get(filePath).toAbsolutePath();
        try(FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)){
            long fileSize = fileChannel.size();
            ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);

            fileChannel.read(buffer);
            buffer.flip();

            return StandardCharsets.UTF_8.decode(buffer).toString();
        } catch (IOException e){
            throw new IllegalStateException("Failed to read key file", e);
        }
    }
}
