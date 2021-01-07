package com.zhokhov.dumper.cli;

import com.zhokhov.dumper.cli.command.ExportCommand;
import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.annotation.Prototype;
import picocli.CommandLine.Command;

@Command(
        name = "dumper",
        // TODO docs
        description = "TODO",
        mixinStandardHelpOptions = true,
        subcommands = {
                ExportCommand.class
        }
)
@Prototype
public class DumperCommand implements Runnable {

    public static void main(String[] args) throws Exception {
        int exitCode = PicocliRunner.execute(DumperCommand.class, args);
        System.exit(exitCode);
    }

    public void run() {
        System.out.println("Hello from Dumper!");
    }

}
