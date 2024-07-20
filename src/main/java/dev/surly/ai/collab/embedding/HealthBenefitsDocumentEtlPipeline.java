package dev.surly.ai.collab.embedding;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HealthBenefitsDocumentEtlPipeline {

    private final VectorStore vectorStore;

    @Value("classpath:documents/health-benefits.pdf")
    Resource healthBenefitsPdfFile;

    @PostConstruct
    public void run() {
        log.info("Running health benefits document ETL pipeline");
        var pdfReader = new PagePdfDocumentReader(healthBenefitsPdfFile);
        vectorStore.add(pdfReader.get());
        log.info("Health benefits document ETL pipeline complete");
    }
}
