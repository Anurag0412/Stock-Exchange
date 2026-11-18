import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class Run {
    // Supplies safe default return values for the JDBC interfaces used by the demo.
    static Object stub(Class<?> type) {
        return Proxy.newProxyInstance(Run.class.getClassLoader(), new Class<?>[]{type}, (p, m, a) -> {
            Class<?> r = m.getReturnType();
            if (m.getName().equals("next")) return false;
            if (r == void.class) return null;
            if (r == boolean.class) return false;
            if (r == int.class) return 0;
            if (r == long.class) return 0L;
            if (r == double.class) return 0.0;
            if (r.isArray()) return java.lang.reflect.Array.newInstance(r.getComponentType(), 0);
            if (r == PreparedStatement.class || r == Statement.class || r == ResultSet.class || r == Connection.class) return stub(r);
            return null;
        });
    }
    // Lets the portfolio app run against the stub connections without a live database.
    static class Drv implements Driver {
        public Connection connect(String u, Properties p) { return u.startsWith("jdbc:postgresql") ? (Connection) stub(Connection.class) : null; }
        public boolean acceptsURL(String u) { return u.startsWith("jdbc:postgresql"); }
        public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) { return new DriverPropertyInfo[0]; }
        public int getMajorVersion() { return 1; } public int getMinorVersion() { return 0; }
        public boolean jdbcCompliant() { return false; }
        public Logger getParentLogger() { return null; }
    }
    // Replays a file of input lines as console input while echoing them to the screen.
    static class EchoIn extends InputStream {
        final Deque<String> lines; byte[] cur = new byte[0]; int pos = 0;
        EchoIn(List<String> l) { lines = new ArrayDeque<>(l); }
        public int read() { byte[] b = new byte[1]; int n = read(b, 0, 1); return n < 0 ? -1 : b[0]; }
        public int read(byte[] b, int off, int len) {
            if (pos >= cur.length) {
                if (lines.isEmpty()) return -1;
                String l = lines.poll(); System.out.println(l);
                cur = (l + "\n").getBytes(); pos = 0;
            }
            int n = Math.min(len, cur.length - pos);
            System.arraycopy(cur, pos, b, off, n); pos += n; return n;
        }
    }
    // Usage: Run <main|practical> <input-file>.
    public static void main(String[] args) throws Exception {
        List<String> in = Files.readAllLines(Path.of(args[1]));
        System.setIn(new EchoIn(in));
        if (args[0].equals("main")) DriverManager.registerDriver(new Drv());
        Class<?> c = Class.forName(args[0].equals("main") ? "Main" : "JavaPracticalUpdate");
        c.getMethod("main", String[].class).invoke(null, (Object) new String[0]);
    }
}
