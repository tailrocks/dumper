/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
