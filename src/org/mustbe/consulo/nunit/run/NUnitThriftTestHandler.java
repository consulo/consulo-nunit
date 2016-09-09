/*
 * Copyright 2013-2014 must-be.org
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

package org.mustbe.consulo.nunit.run;


import org.apache.thrift.TException;
import com.intellij.execution.testframework.sm.runner.GeneralTestEventsProcessor;
import com.intellij.openapi.util.Comparing;
import consulo.execution.testframework.thrift.runner.BaseThriftTestHandler;

/**
 * @author VISTALL
 * @since 18.05.14
 */
public class NUnitThriftTestHandler extends BaseThriftTestHandler
{
	private String myRoot;

	public NUnitThriftTestHandler(GeneralTestEventsProcessor eventsProcessor)
	{
		super(eventsProcessor);
	}

	@Override
	public void suiteStarted(String name, String location) throws TException
	{
		if(myRoot == null)
		{
			myRoot = name;
			return;
		}
		super.suiteStarted(name, location);
	}

	@Override
	public void suiteFinished(String name) throws TException
	{
		if(Comparing.equal(name, myRoot))
		{
			return;
		}
		super.suiteFinished(name);
	}
}
