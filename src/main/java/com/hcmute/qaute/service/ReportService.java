package com.hcmute.qaute.service;

import com.hcmute.qaute.dto.QuestionResponseDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {

    public void exportDashboardPdf(List<QuestionResponseDTO> questions,
            HttpServletResponse response) throws IOException {
        try {
            // 1. Setup Document
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // 2. Load Unicode Font (Tiếng Việt OK)
            BaseFont baseFont = BaseFont.createFont(
                    "fonts/DejaVuSans.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED);

            Font fontTitle = new Font(baseFont, 18, Font.BOLD);
            Font fontHeader = new Font(baseFont, 12, Font.BOLD);
            Font fontRow = new Font(baseFont, 10);

            // 3. Title
            Paragraph title = new Paragraph("QAUTE DASHBOARD REPORT", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(
                    "Generated at: " + java.time.LocalDateTime.now(),
                    fontRow));
            document.add(new Paragraph(
                    "Total Questions: " + questions.size(),
                    fontRow));
            document.add(new Paragraph(" ", fontRow));

            // 4. Table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100f);
            table.setWidths(new float[] { 1f, 3f, 2f, 1.5f, 1.5f });
            table.setSpacingBefore(10);

            // Header
            addHeader(table, "ID", fontHeader);
            addHeader(table, "Title", fontHeader);
            addHeader(table, "Student", fontHeader);
            addHeader(table, "Department", fontHeader);
            addHeader(table, "Status", fontHeader);

            // Rows
            for (QuestionResponseDTO q : questions) {
                table.addCell(new Phrase(String.valueOf(q.getId()), fontRow));
                table.addCell(new Phrase(q.getTitle(), fontRow));
                table.addCell(new Phrase(q.getStudentName(), fontRow));
                table.addCell(new Phrase(
                        q.getDepartmentName() != null ? q.getDepartmentName() : "N/A",
                        fontRow));
                table.addCell(new Phrase(q.getStatus().name(), fontRow));
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            throw new IOException(e);
        }
    }

    private void addHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        table.addCell(cell);
    }
}
