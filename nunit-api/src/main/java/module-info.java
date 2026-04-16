/**
 * @author VISTALL
 * @since 11-Sep-22
 */
module consulo.nunit.api
{
	requires transitive consulo.execution.api;
	requires consulo.process.api;
	requires transitive consulo.dotnet.api;
	requires consulo.internal.dotnet.asm;

	exports consulo.nunit;
	exports consulo.nunit.bundle;
	exports consulo.nunit.icon;
	exports consulo.nunit.localize;
	exports consulo.nunit.module.extension;
}