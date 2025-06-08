package com.learn.ai.spring_ai.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.List;

@Configuration
public class VectorLoader {

    @Value("classpath:/Indian Constitution.pdf")
    private Resource pdfResource;

    /**
     * This method creates a SimpleVectorStore bean that loads or creates a vector store from a PDF document.
     * If the vector store file exists, it loads the vector store from the file.
     * If the vector store file does not exist, it reads the PDF document, splits it into pages,
     * and adds the documents to the vector store, which is then saved to a file.
     *
     * @param embeddingModel The embedding model used for creating the vector store.
     * @return A SimpleVectorStore instance.
     */
    @Bean
    SimpleVectorStore vectorStore(EmbeddingModel embeddingModel) {

        // Load or create a vector store from the PDF document
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();

        // Check if the vector store file exists
        File vectorStoreFile = new File("C:\\Users\\avik\\Codes\\2\\Spring AI\\spring-ai\\src\\main\\resources\\vector_store.json");
        if (vectorStoreFile.exists()) {
            System.out.println("Loading vector store from file!");
            // Load the vector store from the file
            vectorStore.load(vectorStoreFile);
        } else {
            System.out.println("Vector store file not found, creating a new one!");

            // Read the PDF document and split it into pages
            PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
                    .withPagesPerDocument(1)
                    .build();

            // Create a PDF document reader
            PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(pdfResource, pdfDocumentReaderConfig);

            // Read the PDF document
            var textSplitter = new TokenTextSplitter();
            // Set the maximum number of tokens per chunk
            List<Document> documents = textSplitter.apply(pdfDocumentReader.get());

            // Add the documents to the vector store
            vectorStore.add(documents);
            // Save the vector store to a file
            vectorStore.save(vectorStoreFile);

            System.out.println("Vector store created and saved to file!");
        }
        return vectorStore;
    }
}
