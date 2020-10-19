package pl.skyterix.sadsky.user.domain.group;

public sealed interface Permissions permits Permission, SelfPermission {
}
