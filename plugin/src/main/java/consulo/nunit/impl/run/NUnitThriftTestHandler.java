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

import consulo.execution.test.sm.runner.GeneralTestEventsProcessor;
import consulo.execution.test.thrift.runner.BaseThriftTestHandler;
import consulo.util.lang.Comparing;
import org.apache.thrift.TException;

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
