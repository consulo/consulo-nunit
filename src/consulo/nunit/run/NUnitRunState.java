/*
 * Copyright 2013-2015 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consulo.nunit.run;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import consulo.dotnet.run.PatchableRunProfileState;
import consulo.execution.testframework.thrift.runner.ThriftTestExecutionUtil;
import consulo.execution.testframework.thrift.runner.ThriftTestHandlerFactory;

/**
 * @author VISTALL
 * @since 11.01.15
 */
public class NUnitRunState extends PatchableRunProfileState
{
	private final ThriftTestHandlerFactory myFactory;

	public NUnitRunState(@NotNull ExecutionEnvironment executionEnvironment, @NotNull GeneralCommandLine runCommandLine, ThriftTestHandlerFactory factory)
	{
		super(executionEnvironment, runCommandLine);
		myFactory = factory;
	}

	@Nullable
	@Override
	public ExecutionResult executeImpl(Executor executor, @NotNull ProgramRunner programRunner) throws ExecutionException
	{
		TestConsoleProperties testConsoleProperties = new SMTRunnerConsoleProperties((NUnitConfiguration) myExecutionEnvironment.getRunProfile(), "NUnit", executor);

		testConsoleProperties.setIfUndefined(TestConsoleProperties.HIDE_PASSED_TESTS, false);

		final BaseTestsOutputConsoleView smtConsoleView = ThriftTestExecutionUtil.createConsoleWithCustomLocator("NUnit", testConsoleProperties, myExecutionEnvironment, myFactory, null);

		OSProcessHandler osProcessHandler = patchHandler(new OSProcessHandler(getCommandLineForRun()));

		smtConsoleView.attachToProcess(osProcessHandler);

		return new DefaultExecutionResult(smtConsoleView, osProcessHandler);
	}
}
