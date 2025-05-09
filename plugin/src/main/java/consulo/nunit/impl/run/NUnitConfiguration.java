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

import consulo.annotation.access.RequiredReadAction;
import consulo.content.bundle.Sdk;
import consulo.dotnet.compiler.DotNetMacroUtil;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.dotnet.run.coverage.DotNetConfigurationWithCoverage;
import consulo.dotnet.run.impl.coverage.DotNetCoverageConfigurationEditor;
import consulo.dotnet.run.impl.coverage.DotNetCoverageEnabledConfiguration;
import consulo.dotnet.util.DebugConnectionInfo;
import consulo.execution.configuration.*;
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.execution.configuration.ui.SettingsEditorGroup;
import consulo.execution.coverage.CoverageEnabledConfiguration;
import consulo.execution.executor.Executor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.test.sm.runner.GeneralTestEventsProcessor;
import consulo.execution.test.thrift.runner.BaseThriftTestHandler;
import consulo.execution.test.thrift.runner.ThriftTestHandlerFactory;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.nunit.module.extension.NUnitModuleExtension;
import consulo.process.ExecutionException;
import consulo.process.cmd.GeneralCommandLine;
import consulo.util.xml.serializer.InvalidDataException;
import consulo.util.xml.serializer.WriteExternalException;
import org.jdom.Element;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

		CoverageEnabledConfiguration coverageEnabledConfiguration = DotNetCoverageEnabledConfiguration.getOrCreate(this);
		Element coverageElement = new Element("coverage");
		coverageEnabledConfiguration.writeExternal(coverageElement);
		element.addContent(coverageElement);
	}

	@Nonnull
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
	public RunProfileState getState(@Nonnull Executor executor, @Nonnull final ExecutionEnvironment env) throws ExecutionException
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

		DebugConnectionInfo debugConnectionInfo = commandLine.getUserData(DebugConnectionInfo.KEY);
		if(debugConnectionInfo != null)
		{
			state.putUserData(DebugConnectionInfo.KEY, debugConnectionInfo);
		}
		return state;
	}
}
