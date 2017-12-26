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

package consulo.nunit.run;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.GeneralCommandLine;
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
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.compiler.DotNetMacroUtil;
import consulo.dotnet.execution.DebugConnectionInfo;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.dotnet.run.DotNetRunKeys;
import consulo.dotnet.run.coverage.DotNetConfigurationWithCoverage;
import consulo.dotnet.run.coverage.DotNetCoverageConfigurationEditor;
import consulo.dotnet.run.coverage.DotNetCoverageEnabledConfiguration;
import consulo.execution.testframework.thrift.runner.BaseThriftTestHandler;
import consulo.execution.testframework.thrift.runner.ThriftTestHandlerFactory;
import consulo.nunit.module.extension.NUnitModuleExtension;

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
	@RequiredReadAction
	public Collection<Module> getValidModules()
	{
		List<Module> list = new ArrayList<Module>();
		for(Module module : ModuleManager.getInstance(getProject()).getModules())
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
		NUnitConfiguration runProfile = (NUnitConfiguration) env.getRunProfile();

		Module module = runProfile.getConfigurationModule().getModule();
		if(module == null)
		{
			throw new ExecutionException("Module is null");
		}

		final DotNetModuleExtension dotNetModuleExtension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
		if(dotNetModuleExtension == null)
		{
			throw new ExecutionException(".NET module extension is not set");
		}

		Sdk dotNetSdk = dotNetModuleExtension.getSdk();
		if(dotNetSdk == null)
		{
			throw new ExecutionException(".NET SDK is not set");
		}

		final NUnitModuleExtension nUnitModuleExtension = ModuleUtilCore.getExtension(module, NUnitModuleExtension.class);
		if(nUnitModuleExtension == null)
		{
			throw new ExecutionException("NUnit module extension is not set");
		}

		Sdk nunitSdk = nUnitModuleExtension.getSdk();
		if(nunitSdk == null)
		{
			throw new ExecutionException("NUnit SDK is not set");
		}

		String file = DotNetMacroUtil.expandOutputFile(dotNetModuleExtension);
		GeneralCommandLine commandLine = nUnitModuleExtension.createCommandLine(executor, dotNetSdk, nunitSdk);

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

		NUnitRunState state = new NUnitRunState(env, commandLine, factory);

		DebugConnectionInfo debugConnectionInfo = commandLine.getUserData(DotNetRunKeys.DEBUG_CONNECTION_INFO_KEY);
		if(debugConnectionInfo != null)
		{
			state.putUserData(DotNetRunKeys.DEBUG_CONNECTION_INFO_KEY, debugConnectionInfo);
		}
		return state;
	}
}
