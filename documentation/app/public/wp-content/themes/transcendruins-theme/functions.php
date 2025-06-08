<?php

	wp_enqueue_script(
		'custom-utils',
		get_template_directory_uri() . '/assets/js/utils.js',
		array(), null, true);

	function enqueue_documentation_block() {
		wp_register_script(
			'documentation-block-editor',
			get_template_directory_uri() . '/assets/js/block.js',
			array('custom-utils', 'wp-blocks', 'wp-element', 'wp-editor'),
			filemtime(get_template_directory() . '/assets/js/block.js')
		);

		register_block_type('custom/documentation-iframe', array(
			'editor_script' => 'documentation-block-editor',
		));
	}
	add_action('init', 'enqueue_documentation_block');

	function allow_filetype_uploads($mimes) {
		$mimes['json'] = 'application/json';
		$mimes['zip'] = 'application/zip';
		$mimes['trpack'] = 'application/x-trpack';
		return $mimes;
	}
	add_filter('upload_mimes', 'allow_filetype_uploads');
?>