package Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Properties;

public final class PropertiesStore {
    private PropertiesStore() {
        
    }
    /**
     *  โหลดค่าจาก File ที่กำหนด
     * 
     *  @return Properties  
     *  @throws IOEXception หากอ่าน File ล้มเหลาว
     */
    public static Properties load(Path path) throws IOException {
        Properties p = new Properties();
        if (Files.exists(path)) {
            try (InputStream in = Files.newInputStream(path)) {
                p.load(in);
            }
        }
        return p;
    }

    public static void save(Path path, Properties p, String comment) throws IOException {
        FileIO.ensureParentDir(path);
        Path tmp = path.resolveSibling(path.getFileName() + ".tmp");
        try (OutputStream out = Files.newOutputStream(tmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            p.store(out, comment);
        }
        try {
            Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
