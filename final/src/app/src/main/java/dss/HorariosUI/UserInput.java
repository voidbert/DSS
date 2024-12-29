package dss.HorariosUI;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

public class UserInput {
    private Scanner scanner;

    public UserInput() {
        this.scanner = new Scanner(System.in);
    }

    public UserInput(UserInput scanner) {
        this();
    }

    public Object read(String                   prompt,
                       String                   error,
                       Predicate<String>        validate,
                       Function<String, Object> convert) {

        String ret = null;
        do {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (validate.test(line))
                ret = line;
            else if (error != null)
                System.err.println(error);
        } while (ret == null);
        return convert.apply(ret);
    }

    public String readString(String prompt) {
        return (String) this.read(prompt, null, s -> true, s -> s);
    }
}
