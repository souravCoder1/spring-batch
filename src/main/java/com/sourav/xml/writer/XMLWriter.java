package com.sourav.xml.writer;

import com.sourav.jdbc.model.Student;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;


@Component
public class XMLWriter implements ItemWriter<Student> {
    @Override
    public void write(Chunk<? extends Student> chunk) throws Exception {
        System.out.println("Inside Item Writer");
        chunk.getItems().stream().forEach(System.out::println);
    }
}