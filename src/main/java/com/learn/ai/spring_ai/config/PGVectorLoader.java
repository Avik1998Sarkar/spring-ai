package com.learn.ai.spring_ai.config;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class PGVectorLoader {
    @Value("classpath:/Indian Constitution.pdf")
    private Resource pdfResource;

    private final VectorStore vectorStore;
    private final JdbcClient jdbcClient;

    public PGVectorLoader(VectorStore vectorStore, JdbcClient jdbcClient) {
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }

    @PostConstruct
    public void init() {

        Integer count = jdbcClient.sql("select count(*) from vector_store")
                .query(Integer.class)
                .single();
        System.out.println("Vector Store Count: " + count);
        if (count == 0) {
            System.out.println("Vector store is empty, loading from PDF document!");

            PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
                    .withPagesPerDocument(1)
                    .build();

            PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(pdfResource, pdfDocumentReaderConfig);

            var textSplitter = new TokenTextSplitter();

            vectorStore.accept(textSplitter.apply(pdfDocumentReader.get()));

            System.out.println("Saving vector store to file...");
        } else {
            System.out.println("Vector store already contains data, skipping loading from PDF document.");
        }

    }
}
