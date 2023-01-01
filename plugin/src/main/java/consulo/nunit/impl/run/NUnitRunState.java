/*
 * Copyright 2013-2017 consulo.io
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

package consulo.nunit.impl.run;

import consulo.dotnet.run.PatchableRunProfileState;
import consulo.execution.DefaultExecutionResult;
import consulo.execution.ExecutionResult;
import consulo.execution.executor.Executor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.runner.ProgramRunner;
import consulo.execution.test.TestConsoleProperties;
import consulo.execution.test.sm.runner.SMTRunnerConsoleProperties;
import consulo.execution.test.thrift.runner.ThriftTestExecutionUtil;
import consulo.execution.test.thrift.runner.ThriftTestHandlerFactory;
import consulo.execution.test.ui.BaseTestsOutputConsoleView;
import consulo.process.ExecutionException;
import consulo.process.ProcessHandler;
import consulo.process.cmd.GeneralCommandLine;
import consulo.process.local.ProcessHandlerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 11.01.15
 */
public class NUnitRunState extends PatchableRunProfileState
{
	private final ThriftTestHandlerFactory myFactory;

	public NUnitRunState(@Nonnull ExecutionEnvironment executionEnvironment, @Nonnull GeneralCommandLine runCommandLine, ThriftTestHandlerFactory factory)
	{
		super(executionEnvironment, runCommandLine);
		myFactory = factory;
	}

	@Nullable
	@Override
	public ExecutionResult executeImpl(Executor executor, @Nonnull ProgramRunner programRunner) throws ExecutionException
	{
		TestConsoleProperties testConsoleProperties = new SMTRunnerConsoleProperties((NUnitConfiguration) myExecutionEnvironment.getRunProfile(), "NUnit", executor);

		testConsoleProperties.setIfUndefined(TestConsoleProperties.HIDE_PASSED_TESTS, false);

		final BaseTestsOutputConsoleView smtConsoleView = ThriftTestExecutionUtil.createConsoleWithCustomLocator("NUnit", testConsoleProperties, myExecutionEnvironment, myFactory, null);

		ProcessHandler osProcessHandler = patchHandler(ProcessHandlerFactory.getInstance().createProcessHandler(getCommandLineForRun()));

		smtConsoleView.attachToProcess(osProcessHandler);

		return new DefaultExecutionResult(smtConsoleView, osProcessHandler);
	}
}
