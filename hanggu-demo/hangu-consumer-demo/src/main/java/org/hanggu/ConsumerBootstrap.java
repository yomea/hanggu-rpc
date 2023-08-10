package org.hanggu;

import com.hanggu.consumer.annotation.ReferenceScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wuzhenhong
 */

@SpringBootApplication
@ReferenceScan
public class ConsumerBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerBootstrap.class, args);
    }
}