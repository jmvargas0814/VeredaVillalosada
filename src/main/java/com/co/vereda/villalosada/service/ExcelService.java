package com.co.vereda.villalosada.service;

import com.co.vereda.villalosada.model.PagoMensual;
import com.co.vereda.villalosada.model.Usuario;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelService {

  public byte[] generarExcelPagos(Usuario usuario, List<PagoMensual> pagos, int anio)
      throws Exception {

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Reporte Pagos");

    CellStyle headerStyle = crearHeaderStyle(workbook);
    CellStyle tableHeaderStyle = crearTableHeaderStyle(workbook);
    CellStyle normalStyle = crearNormalStyle(workbook);
    CellStyle paidStyle = crearPaidStyle(workbook);
    CellStyle dueStyle = crearDueStyle(workbook);

    int rowNum = 0;

    Row titleRow = sheet.createRow(rowNum++);
    Cell titleCell = titleRow.createCell(0);
    titleCell.setCellValue("VEREDA VILLALOSADA - REPORTE DE PAGOS");
    titleCell.setCellStyle(headerStyle);
    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));

    rowNum++;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    rowNum =
        crearFilaInfo(
            sheet,
            rowNum,
            "Nombre Completo:",
            usuario.getNombres() + " " + usuario.getApellidos(),
            normalStyle);

    rowNum =
        crearFilaInfo(sheet, rowNum, "Documento:", usuario.getNumeroIdentificacion(), normalStyle);

    rowNum =
        crearFilaInfo(
            sheet,
            rowNum,
            "Fecha Ingreso:",
            usuario.getFechaRegistro().toLocalDate().format(formatter),
            normalStyle);

    rowNum = crearFilaInfo(sheet, rowNum, "Año Reporte:", String.valueOf(anio), normalStyle);

    rowNum++;

    pagos.sort(Comparator.comparing(PagoMensual::getMes));

    Row header = sheet.createRow(rowNum++);
    header.createCell(0).setCellValue("Mes");
    header.createCell(1).setCellValue("Año");
    header.createCell(2).setCellValue("Estado");

    for (int i = 0; i < 3; i++) {
      header.getCell(i).setCellStyle(tableHeaderStyle);
    }

    for (PagoMensual pago : pagos) {

      Row row = sheet.createRow(rowNum++);

      row.createCell(0).setCellValue(java.time.Month.of(pago.getMes()).name());

      row.createCell(1).setCellValue(pago.getAnio());

      Cell estadoCell = row.createCell(2);

      if (Boolean.TRUE.equals(pago.getPagado())) {
        estadoCell.setCellValue("PAGADO");
        estadoCell.setCellStyle(paidStyle);
      } else {
        estadoCell.setCellValue("DEBE");
        estadoCell.setCellStyle(dueStyle);
      }

      row.getCell(0).setCellStyle(normalStyle);
      row.getCell(1).setCellStyle(normalStyle);
    }

    for (int i = 0; i < 3; i++) {
      sheet.autoSizeColumn(i);
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    workbook.close();

    return out.toByteArray();
  }

  private int crearFilaInfo(Sheet sheet, int rowNum, String label, String value, CellStyle style) {

    Row row = sheet.createRow(rowNum++);
    row.createCell(0).setCellValue(label);
    row.createCell(1).setCellValue(value);

    row.getCell(0).setCellStyle(style);
    row.getCell(1).setCellStyle(style);

    return rowNum;
  }

  private CellStyle crearHeaderStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBold(true);
    font.setFontHeightInPoints((short) 14);
    style.setFont(font);
    return style;
  }

  private CellStyle crearTableHeaderStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);

    Font font = workbook.createFont();
    font.setBold(true);
    style.setFont(font);

    return style;
  }

  private CellStyle crearNormalStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    return style;
  }

  private CellStyle crearPaidStyle(Workbook workbook) {
    CellStyle style = crearNormalStyle(workbook);
    Font font = workbook.createFont();
    font.setColor(IndexedColors.GREEN.getIndex());
    font.setBold(true);
    style.setFont(font);
    return style;
  }

  private CellStyle crearDueStyle(Workbook workbook) {
    CellStyle style = crearNormalStyle(workbook);
    Font font = workbook.createFont();
    font.setColor(IndexedColors.RED.getIndex());
    font.setBold(true);
    style.setFont(font);
    return style;
  }

  public byte[] generarExcelUsuarios(List<Usuario> usuarios) throws Exception {

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Reporte General");

    CellStyle headerStyle = crearHeaderStyle(workbook);
    CellStyle tableHeaderStyle = crearTableHeaderStyle(workbook);
    CellStyle normalStyle = crearNormalStyle(workbook);
    CellStyle paidStyle = crearPaidStyle(workbook);
    CellStyle dueStyle = crearDueStyle(workbook);

    int rowNum = 0;

    int anioActual = java.time.LocalDate.now().getYear();
    int mesActual = java.time.LocalDate.now().getMonthValue();

    Row titleRow = sheet.createRow(rowNum++);
    Cell titleCell = titleRow.createCell(0);
    titleCell.setCellValue("VEREDA VILLALOSADA - REPORTE GENERAL DE PAGOS " + anioActual);
    titleCell.setCellStyle(headerStyle);

    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 1 + mesActual));

    rowNum++;

    Row header = sheet.createRow(rowNum++);

    header.createCell(0).setCellValue("Código");
    header.createCell(1).setCellValue("Nombre Completo");

    header.getCell(0).setCellStyle(tableHeaderStyle);
    header.getCell(1).setCellStyle(tableHeaderStyle);

    int colIndex = 2;

    for (int mes = 1; mes <= mesActual; mes++) {
      Cell cell = header.createCell(colIndex);
      cell.setCellValue(java.time.Month.of(mes).name().substring(0, 3));
      cell.setCellStyle(tableHeaderStyle);
      colIndex++;
    }

    for (Usuario usuario : usuarios) {

      Row row = sheet.createRow(rowNum++);

      Cell codigoCell = row.createCell(0);
      codigoCell.setCellValue(usuario.getCodigoUsuario());
      codigoCell.setCellStyle(normalStyle);

      Cell nombreCell = row.createCell(1);
      nombreCell.setCellValue(usuario.getNombres() + " " + usuario.getApellidos());
      nombreCell.setCellStyle(normalStyle);

      int mesInicio = usuario.getFechaRegistro().getMonthValue();

      Map<Integer, PagoMensual> pagosPorMes =
          usuario.getPagos().stream()
              .filter(p -> p.getAnio().equals(anioActual))
              .collect(
                  java.util.stream.Collectors.toMap(PagoMensual::getMes, p -> p, (p1, p2) -> p1));

      colIndex = 2;

      for (int mes = 1; mes <= mesActual; mes++) {

        Cell cell = row.createCell(colIndex);

        if (mes < mesInicio) {

          cell.setCellValue("-");
          cell.setCellStyle(normalStyle);

        } else {

          PagoMensual pago = pagosPorMes.get(mes);

          if (pago != null && Boolean.TRUE.equals(pago.getPagado())) {
            cell.setCellValue("PAGADO");
            cell.setCellStyle(paidStyle);
          } else {
            cell.setCellValue("DEBE");
            cell.setCellStyle(dueStyle);
          }
        }

        colIndex++;
      }
    }
    for (int i = 0; i < 2 + mesActual; i++) {
      sheet.autoSizeColumn(i);
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    workbook.close();

    return out.toByteArray();
  }
}
