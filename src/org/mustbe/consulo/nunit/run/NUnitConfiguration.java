package org.mustbe.consulo.nunit.run;

import java.util.ArrayList;
import java.util.Collection;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.compiler.DotNetMacros;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.execution.testframework.thrift.runner.BaseThriftTestHandler;
import org.mustbe.consulo.execution.testframework.thrift.runner.ThriftTestExecutionUtil;
import org.mustbe.consulo.execution.testframework.thrift.runner.ThriftTestHandlerFactory;
import org.mustbe.consulo.nunit.module.extension.NUnitModuleExtension;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.GeneralTestEventsProcessor;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.03.14
 */
public class NUnitConfiguration extends ModuleBasedConfiguration<RunConfigurationModule>
{
	public NUnitConfiguration(String name, RunConfigurationModule configurationModule, ConfigurationFactory factory)
	{
		super(name, configurationModule, factory);
	}

	@Override
	public Collection<Module> getValidModules()
	{
		val list = new ArrayList<Module>();
		for(val module : ModuleManager.getInstance(getProject()).getModules())
		{
			if(ModuleUtilCore.getExtension(module, NUnitModuleExtension.class) != null)
			{
				list.add(module);
			}
		}
		return list;
	}

	@Override
	public void readExternal(Element element) throws InvalidDataException
	{
		super.readExternal(element);
		readModule(element);
	}

	@Override
	public void writeExternal(Element element) throws WriteExternalException
	{
		super.writeExternal(element);
		writeModule(element);
	}

	@NotNull
	@Override
	public SettingsEditor<? extends RunConfiguration> getConfigurationEditor()
	{
		return new NUnitConfigurationEditor(getProject());
	}

	@Nullable
	@Override
	public RunProfileState getState(@NotNull Executor executor, @NotNull final ExecutionEnvironment env) throws ExecutionException
	{
		val runProfile = (NUnitConfiguration) env.getRunProfile();

		val module = runProfile.getConfigurationModule().getModule();
		if(module == null)
		{
			throw new ExecutionException("Module is null");
		}

		final DotNetModuleExtension dotNetModuleExtension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
		if(dotNetModuleExtension == null)
		{
			throw new ExecutionException(".NET module extension is not set");
		}
		final NUnitModuleExtension nUnitModuleExtension = ModuleUtilCore.getExtension(module, NUnitModuleExtension.class);
		if(nUnitModuleExtension == null)
		{
			throw new ExecutionException("NUnit module extension is not set");
		}

		return new RunProfileState()
		{
			@Nullable
			@Override
			public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException
			{
				val file = DotNetMacros.extract(module, dotNetModuleExtension);
				val commandLine = nUnitModuleExtension.createCommandLine();

				ThriftTestHandlerFactory factory = new ThriftTestHandlerFactory()
				{
					@Override
					public BaseThriftTestHandler createHandler(GeneralTestEventsProcessor processor)
					{
						return new NUnitThriftTestHandler(processor);
					}
				};

				commandLine.addParameter("consulo_nunit_wrapper.Program");
				commandLine.addParameter(file);
				commandLine.addParameter(String.valueOf(factory.getPort()));

				TestConsoleProperties testConsoleProperties = new SMTRunnerConsoleProperties((NUnitConfiguration) env.getRunProfile(), "NUnit",
						executor);

				testConsoleProperties.setIfUndefined(TestConsoleProperties.HIDE_PASSED_TESTS, false);

				final BaseTestsOutputConsoleView smtConsoleView = ThriftTestExecutionUtil.createConsoleWithCustomLocator("NUnit",
						testConsoleProperties, env, factory, null);

				OSProcessHandler osProcessHandler = new OSProcessHandler(commandLine);

				smtConsoleView.attachToProcess(osProcessHandler);

				return new DefaultExecutionResult(smtConsoleView, osProcessHandler);
			}
		};
	}
}
