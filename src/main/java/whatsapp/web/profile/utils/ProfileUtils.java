package whatsapp.web.profile.utils;

import java.util.Base64;
import org.springframework.stereotype.Component;
import whatsapp.web.profile.enums.UnitSizeFile;

import java.io.IOException;


@Component
public class ProfileUtils {

    public Integer calculateSizeFileInBytes(String base64File) {
        // Decodifica o arquivo de Base64 para um byte array
        byte[] decodedBytes = Base64.getDecoder().decode(base64File);

        // Obtemos o tamanho do arquivo original (em bytes)
        return decodedBytes.length;
    }

    public static String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B"; // Menor que 1 KB
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0); // Menor que 1 MB
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024)); // Menor que 1 GB
        } else if (bytes < 1024L * 1024 * 1024 * 1024) {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024)); // Menor que 1 TB
        } else {
            return String.format("%.2f TB", bytes / (1024.0 * 1024 * 1024 * 1024)); // Maior que 1 TB
        }
    }

    public Double convertToBytes(Double value, UnitSizeFile unit) throws IOException {
        // Converte conforme a unidade
        switch (unit) {
            case UnitSizeFile.B:
                return value; // Já está em bytes, não precisa fazer nada
            case UnitSizeFile.KB:
                return value * 1024; // 1 KB = 1024 bytes
            case UnitSizeFile.MB:
                return value * 1024 * 1024; // 1 MB = 1024 * 1024 bytes
            case UnitSizeFile.GB:
                return value * 1024 * 1024 * 1024; // 1 GB = 1024 * 1024 * 1024 bytes
            case UnitSizeFile.TB:
                return value * 1024 * 1024 * 1024 * 1024; // 1 TB = 1024 * 1024 * 1024 * 1024 bytes
            default:
                throw new IOException("Unidade desconhecida: " + unit);
        }
    }

}
