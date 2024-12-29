package consulo.nunit.mono.module.extension;

import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.content.layer.ModuleExtensionProvider;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.ModuleExtension;
import consulo.module.extension.MutableModuleExtension;
import consulo.nunit.icon.NUnitIconGroup;
import consulo.ui.image.Image;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 11-Sep-22
 */
@ExtensionImpl
public class MonoNUnitModuleExtensionProvider implements ModuleExtensionProvider<MonoNUnitModuleExtension>
{
	@Nonnull
	@Override
	public String getId()
	{
		return "mono-nunit";
	}

	@Nullable
	@Override
	public String getParentId()
	{
		return "mono-dotnet";
	}

	@Nonnull
	@Override
	public LocalizeValue getName()
	{
		return LocalizeValue.localizeTODO("NUnit");
	}

	@Nonnull
	@Override
	public Image getIcon()
	{
		return NUnitIconGroup.nunit();
	}

	@Nonnull
	@Override
	public ModuleExtension<MonoNUnitModuleExtension> createImmutableExtension(@Nonnull ModuleRootLayer moduleRootLayer)
	{
		return new MonoNUnitModuleExtension(getId(), moduleRootLayer);
	}

	@Nonnull
	@Override
	public MutableModuleExtension<MonoNUnitModuleExtension> createMutableExtension(@Nonnull ModuleRootLayer moduleRootLayer)
	{
		return new MonoNUnitMutableModuleExtension(getId(), moduleRootLayer);
	}
}
