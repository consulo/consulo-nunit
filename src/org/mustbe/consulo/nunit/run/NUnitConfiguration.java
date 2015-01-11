package org.mustbe.consulo.nunit.run;

import java.util.ArrayList;
import java.util.Collection;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.compiler.DotNetMacroUtil;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.run.coverage.DotNetConfigurationWithCoverage;
import org.mustbe.consulo.dotnet.run.coverage.DotNetCoverageConfigurationEditor;
import org.mustbe.consulo.dotnet.run.coverage.DotNetCoverageEnabledConfiguration;
import org.mustbe.consulo.execution.testframework.thrift.runner.BaseThriftTestHandler;
import org.mustbe.consulo.execution.testframework.thrift.runner.ThriftTestHandlerFactory;
import org.mustbe.consulo.nunit.module.extension.NUnitModuleExtension;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.sm.runner.GeneralTestEventsProcessor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.03.14
 */
public class NUnitConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> implements DotNetConfigurationWithCoverage
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

		Element coverageElement = element.getChild("coverage");
		if(coverageElement != null)
		{
			CoverageEnabledConfiguration coverageEnabledConfiguration = DotNetCoverageEnabledConfiguration.getOrCreate(this);
			coverageEnabledConfiguration.readExternal(coverageElement);
		}
	}

	@Override
	public void writeExternal(Element element) throws WriteExternalException
	{
		super.writeExternal(element);
		writeModule(element);

		CoverageEnabledConfiguration coverageEnabledConfiguration = DotNetCoverageEnabledConfiguration.getOrCreate(this);
		Element coverageElement = new Element("coverage");
		coverageEnabledConfiguration.writeExternal(coverageElement);
		element.addContent(coverageElement);
	}

	@NotNull
	@Override
	@SuppressWarnings("unchecked")
	public SettingsEditor<? extends RunConfiguration> getConfigurationEditor()
	{
		SettingsEditorGroup group = new SettingsEditorGroup();
		group.addEditor("General", new NUnitConfigurationEditor(getProject()));
		group.addEditor("Coverage", new DotNetCoverageConfigurationEditor());
		return group;
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

		val file = DotNetMacroUtil.expandOutputFile(dotNetModuleExtension);
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

		return new NUnitRunState(env, commandLine, factory);
	}
}
