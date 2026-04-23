package task2.registro_movimentacoes.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import task2.registro_movimentacoes.model.Lancamento;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public byte[] gerarRelatorioLancamentos(List<Lancamento> lancamentos) 
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        
        try 
        {
            PdfWriter.getInstance(document, out);
            document.open();

            Color corPrimaria = new Color(79, 70, 229);
            Color corHeader = new Color(30, 41, 59);
            Color corLinhaPar = new Color(248, 250, 252);

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, corPrimaria);
            Paragraph titulo = new Paragraph("Relatório de Lançamentos Financeiros", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(30);
            document.add(titulo);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 1.5f, 1.5f, 1.5f, 1.5f}); 

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
            String[] headers = {"Descrição", "Data", "Valor", "Tipo", "Situação"};
            
            for (String header : headers) 
            {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                cell.setBackgroundColor(corHeader);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(10f);
                cell.setBorderColor(new Color(229, 231, 235)); 
                table.addCell(cell);
            }

            Font fontDado = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(31, 41, 55));
            boolean isPar = false;

            for (Lancamento l : lancamentos) 
            {
                Color bgColor = isPar ? corLinhaPar : Color.WHITE;

                String desc = l.getDescricao() != null ? l.getDescricao() : "-";
                String data = l.getDataLancamento() != null ? l.getDataLancamento().toString() : "-";
                String valor = l.getValor() != null ? "R$ " + String.format("%.2f", l.getValor()) : "-";
                String tipo = l.getTipo() != null ? l.getTipo().toString() : "-";
                String situacao = l.getSituacao() != null ? l.getSituacao().toString() : "-";

                String[] dados = {desc, data, valor, tipo, situacao};

                for (String dado : dados) 
                {
                    PdfPCell cell = new PdfPCell(new Phrase(dado, fontDado));
                    cell.setBackgroundColor(bgColor);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setPadding(8f);
                    cell.setBorderColor(new Color(229, 231, 235));
                    table.addCell(cell);
                }
                isPar = !isPar; 
            }

            document.add(table);
            document.close();
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}