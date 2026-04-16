/**
 * @author VISTALL
 * @since 11-Sep-22
 */
module consulo.nunit
{
	requires transitive consulo.nunit.api;
	requires transitive consulo.dotnet.psi.api;
	requires transitive consulo.dotnet.impl;
	requires transitive consulo.dotnet.execution.api;
	requires transitive consulo.dotnet.execution.impl;

	requires consulo.execution.test.sm.api;
	requires consulo.execution.test.thrift.api;
	requires org.apache.thrift;
}