package yuuria.stackupper.configlibrary;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yuuria.stackupper.configlibrary.ast.Listener;
import yuuria.stackupper.language.StackUpperLexer;
import yuuria.stackupper.language.StackUpperParser;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class ConfigLibrary {
    public static final Logger logger = LoggerFactory.getLogger("stackupperconfiglib");
    public static void addFile(File file)
    {
        logger.info("checking file {}", file.getAbsolutePath());
        if (file.isDirectory()) {
            for (File file1 : Objects.requireNonNull(file.listFiles((t, d) ->  d.endsWith(".su") || d.endsWith(".stackupper") ))) {
                logger.info("File added: {}", file1.getAbsolutePath());
                Constant.FilesArray.add(file1);
            }
            return;
        }

        if (file.getName().endsWith(".su") || file.getName().endsWith(".stackupper")) {
            logger.info("File added: {}", file.getAbsolutePath());
            Constant.FilesArray.add(file);
        }
    }

    public static void addFile(File file, Boolean clearCached)
    {
        if (clearCached) Constant.FilesArray.clear();
        addFile(file);
    }

    public static void Compile(File file)
    {
        try {
            logger.info("parsing {}", file.getName());
            var lexer = new StackUpperLexer(CharStreams.fromPath(Path.of(file.getPath())));
            var tokens = new CommonTokenStream(lexer);
            var parser = new StackUpperParser(tokens);
            Listener listener = new Listener();

            lexer.removeErrorListeners();
            parser.removeErrorListeners();

            var ctx = parser.start();

            ParseTreeWalker.DEFAULT.walk(listener, ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Start()
    {
        if (!Constant.ItemCollection.isEmpty()) Constant.ItemCollection.clear();
        if (Constant.FilesArray.isEmpty()) {
            logger.error("FilsArray is empty");
            return;
        }
        for (File file : Constant.FilesArray) {
            Compile(file);
        }
    }

}
