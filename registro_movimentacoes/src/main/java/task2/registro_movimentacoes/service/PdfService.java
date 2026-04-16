package task2.registro_movimentacoes.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import task2.registro_movimentacoes.model.Lancamento;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public byte[] gerarRelatorioLancamentos(List<Lancamento> lancamentos) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph titulo = new Paragraph("Relatório de Lançamentos Financeiros", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            // Tabela com 5 colunas
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            // Cabeçalhos
            String[] headers = {"Descrição", "Data", "Valor", "Tipo", "Situação"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Dados
            for (Lancamento l : lancamentos) {
                table.addCell(l.getDescricao());
                table.addCell(l.getDataLancamento().toString());
                table.addCell("R$ " + l.getValor().toString());
                table.addCell(l.getTipo().toString());
                table.addCell(l.getSituacao().toString());
            }

            document.add(table);
            document.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}