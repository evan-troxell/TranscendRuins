function compareVersions(a, b) {
    const aParts = a.split('.').map(Number);
    const bParts = b.split('.').map(Number);

    for (let i = 0; i < Math.max(a.length, b.length); i++) {
        const aVal = aParts[i] ?? 0; // Use 0 if undefined
        const bVal = bParts[i] ?? 0;

        if (aVal < bVal) return 1;
        if (aVal > bVal) return -1;
    }
    return 0;
}

function sortVersions(versions) {
    return versions.sort(compareVersions);
}

function getLatestVersion(versions) {
    const sortedVersions = sortVersions(versions);
    return sortedVersions[0];
}
