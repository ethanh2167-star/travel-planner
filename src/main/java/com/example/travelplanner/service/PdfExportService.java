package com.example.travelplanner.service;

import com.example.travelplanner.entity.Trip;
import com.example.travelplanner.entity.TripItem;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PdfExportService {

    private static final DeviceRgb PRIMARY   = new DeviceRgb(0x2B, 0x6C, 0xB0);
    private static final DeviceRgb SECONDARY = new DeviceRgb(0xEB, 0xF8, 0xFF);
    private static final DeviceRgb ACCENT    = new DeviceRgb(0x2F, 0x85, 0x5A);
    private static final DeviceRgb GRAY      = new DeviceRgb(0x71, 0x71, 0x71);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DT_FMT   = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    public byte[] generateTripPdf(Trip trip) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf  = new PdfDocument(writer);
            Document doc     = new Document(pdf, PageSize.A4);
            doc.setMargins(40, 40, 40, 40);

            PdfFont font = loadChineseFont();


            Table header = new Table(UnitValue.createPercentArray(new float[]{1}))
                    .useAllAvailableWidth()
                    .setBackgroundColor(PRIMARY)
                    .setPadding(16);

            header.addCell(new Cell().setBorder(null)
                    .add(new Paragraph(trip.getTitle())
                            .setFont(font).setFontSize(22)
                            .setFontColor(ColorConstants.WHITE).setBold()
                            .setMarginBottom(4)));

            String subtitle = "  " + trip.getDestination() + "   "
                    + trip.getStartDate().format(DATE_FMT)
                    + " ~ " + trip.getEndDate().format(DATE_FMT)
                    + "  (" + trip.getDurationDays() + " 天)";
            header.addCell(new Cell().setBorder(null)
                    .add(new Paragraph(subtitle)
                            .setFont(font).setFontSize(11)
                            .setFontColor(SECONDARY)));
            doc.add(header);
            doc.add(new Paragraph(" ").setFontSize(6));


            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                    .useAllAvailableWidth()
                    .setBackgroundColor(SECONDARY)
                    .setBorder(new SolidBorder(PRIMARY, 0.5f))
                    .setPadding(10).setMarginBottom(16);

            addInfoCell(infoTable, "出發日期", trip.getStartDate().format(DATE_FMT), font);
            addInfoCell(infoTable, "返回日期", trip.getEndDate().format(DATE_FMT), font);
            addInfoCell(infoTable, "預算",
                    trip.getBudget() != null ? "NT$ " + trip.getBudget().toPlainString() : "未設定",
                    font);
            doc.add(infoTable);


            if (trip.getDescription() != null && !trip.getDescription().isBlank()) {
                doc.add(sectionTitle("行程說明", font));
                doc.add(new Paragraph(trip.getDescription())
                        .setFont(font).setFontSize(11)
                        .setFontColor(GRAY).setMarginBottom(14));
            }


            if (trip.getItems() != null && !trip.getItems().isEmpty()) {
                doc.add(sectionTitle("每日行程", font));


                Map<Integer, ArrayList<TripItem>> byDay = new LinkedHashMap<>();
                for (TripItem item : trip.getItems()) {
                    byDay.computeIfAbsent(item.getDayNumber(), k -> new ArrayList<>()).add(item);
                }

                for (Map.Entry<Integer, ArrayList<TripItem>> entry : byDay.entrySet()) {
                    int day = entry.getKey();

                    doc.add(new Paragraph("Day " + day)
                            .setFont(font).setFontSize(13).setBold()
                            .setFontColor(ColorConstants.WHITE)
                            .setBackgroundColor(ACCENT)
                            .setPadding(6).setMarginBottom(0));

                    Table itemTable = new Table(
                            UnitValue.createPercentArray(new float[]{10, 25, 15, 50}))
                            .useAllAvailableWidth().setMarginBottom(12);

                    String[] headers = {"時間", "地點", "類別", "備註"};
                    for (String h : headers) {
                        itemTable.addHeaderCell(new Cell()
                                .setBackgroundColor(SECONDARY)
                                .setBorder(new SolidBorder(PRIMARY, 0.3f))
                                .add(new Paragraph(h).setFont(font).setFontSize(10)
                                        .setBold().setFontColor(PRIMARY)));
                    }

                    for (TripItem item : entry.getValue()) {
                        String time = item.getItemTime() != null
                                ? item.getItemTime().format(TIME_FMT) : "";

                        String[] values = {
                            time,
                            nvl(item.getPlaceName()),
                            nvl(item.getCategory()),
                            nvl(item.getNote())
                        };
                        for (String v : values) {
                            itemTable.addCell(new Cell()
                                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.3f))
                                    .add(new Paragraph(v).setFont(font).setFontSize(10)));
                        }
                    }
                    doc.add(itemTable);
                }
            }


            doc.add(new Paragraph("\n"));
            doc.add(new Paragraph("本行程由 Travel Planner 自動產生  "
                    + LocalDateTime.now().format(DT_FMT))
                    .setFont(font).setFontSize(9).setFontColor(GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("PDF 產生失敗：" + e.getMessage(), e);
        }
    }

    private PdfFont loadChineseFont() {
        String[] paths = {
            "C:/Windows/Fonts/msjh.ttc,0",
            "C:/Windows/Fonts/msjhbd.ttc,0",
            "C:/Windows/Fonts/mingliu.ttc,0",
            "/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc,0",
            "/Library/Fonts/Heiti.ttc,0",
        };
        for (String path : paths) {
            try {
                return PdfFontFactory.createFont(path, PdfEncodings.IDENTITY_H);
            } catch (Exception ignored) {

            }
        }
        try {
            return PdfFontFactory.createFont(StandardFonts.HELVETICA);
        } catch (Exception e) {
            throw new RuntimeException("無法載入任何字型", e);
        }
    }

    private Paragraph sectionTitle(String text, PdfFont font) {
        return new Paragraph(text)
                .setFont(font).setFontSize(14).setBold()
                .setFontColor(PRIMARY)
                .setBorderBottom(new SolidBorder(PRIMARY, 1))
                .setMarginBottom(8).setMarginTop(4);
    }

    private void addInfoCell(Table table, String label, String value, PdfFont font) {
        Cell cell = new Cell().setBorder(null).setPadding(8);
        cell.add(new Paragraph(label).setFont(font).setFontSize(9).setFontColor(GRAY));
        cell.add(new Paragraph(value).setFont(font).setFontSize(12).setBold()
                .setFontColor(PRIMARY));
        table.addCell(cell);
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }
}