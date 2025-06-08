<?php 
/**
 * Template Name: Download
 * Description: A template for downloading files.
 */
?>

<?php get_header(); ?>

<h1 class="title"><?php the_title(); ?></h1>

<div id="download-container" class="container"></div>
<script src="/wp-content/themes/transcendruins-theme/assets/js/utils.js"></script>
<script>

	async function getFileSize(url) {
		try {
			const response = await fetch(url, { method: 'HEAD' });
			const size = response.headers.get('Content-Length');

			if (size !== null) {

				return parseInt(size, 10);
			} else {

				return "Unknown size";
			}
		} catch (error) {

			return "Error fetching file size";
		}
	}

	function formatBytes(bytes) {

		if (typeof bytes !== 'number') return bytes;

		if (bytes < 0) return 'Invalid size';
		if (bytes === 0) return '0 Bytes';

		const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
		const size = Math.floor(Math.log(bytes) / Math.log(1024));
		if (size >= sizes.length) return 'File too large';

		return `${(bytes / Math.pow(1024, size)).toFixed(2)} ${sizes[size]}`;
	}

	async function createDownloadLink(title, link) {

		if (!link) return '';

		const linkHTML = `<h4 class="game-download-link-title">⤓ Download ${title} (${link.substr(link.lastIndexOf('.'))})</h4>`;

		const fileSize = await getFileSize(link);
		const fileSizeHTML = `<p class="game-download-link-size">File Size: ${formatBytes(fileSize)}</p>`;

		return `
			<a class="game-download-link" href="${link}" target="_blank">
				${linkHTML}
				${fileSizeHTML}
			</a>
		`;
	}

	async function createVersionDownload(data, version) {
		
		const download = document.createElement('div');
			
		download.id = `game-download-${version}-container`;
		download.classList.add('game-download-container');

		const releaseHTML = data.releaseDate ?
			`<p class="game-download-release">Released on ${data.releaseDate}</p>` :
			'';

		const descriptionHTML = data.description ?
			`<p class="game-download-description">Description: ${data.description}</p>` :
			'';

		const highlightFeaturesHTML = data.highlightFeatures ? `<div class="game-download-highlight-features-container">\n<p class="game-download-highlight-features-header">Highlight Features:</p>\n<ul class="game-download-highlight-features">${
			data.highlightFeatures.map(
			(content, _) => `<li class="game-download-highlight-feature">${content}</li>`).join('\n')
			}</ul></div>` :
			'';

		const patchNotesHTML = data.patchNotes ? `<div class="game-download-patch-notes-container">\n<p class="game-download-patch-notes-header">Patch Notes:</p>\n<ul class="game-download-patch-notes">${
			data.patchNotes.map(
			(content, _) => `<li class="game-download-patch-note">${content}</li>`).join('\n')
			}</ul></div>` :
			'';

		const links = data.downloadLinks;
		
		const downloadLinksFormatted = links ?
			await createDownloadLink('Windows', links.exe) +
			await createDownloadLink('Mac', links.dmg) +
			await createDownloadLink('Linux', links.deb) +
			await createDownloadLink('Linux', links.rpm) :
			'';
		
		const downloadLinksHTML = downloadLinksFormatted ?
			`<ul class="game-download-links-container">${downloadLinksFormatted}</ul>` :
			'';

		const baseUrl = window.location.origin;

		download.innerHTML = `
			<h2 class="game-download-header">Version ${version}</h2>
			${releaseHTML}
			${descriptionHTML}
			${highlightFeaturesHTML}
			${patchNotesHTML}
			${downloadLinksHTML}
		`;

		return download;
	}

	async function createVersionDownloads() {
		const file = await fetch('/wp-content/uploads/game_versions/game_versions.json');
		const json = await file.json();

		const container = document.getElementById('download-container');

		const downloads = {};

		for (let version in json) {

			const data = json[version];
			const download = await createVersionDownload(data, version);
			downloads[version] = download;
		}

		const versions = sortVersions(Object.keys(downloads));
		for (let version of versions) {

			const download = downloads[version];
			container.appendChild(download);
		}
	}

	createVersionDownloads();
</script>

<?php
	wp_reset_postdata();
	the_content();
?>

<?php get_footer(); ?>