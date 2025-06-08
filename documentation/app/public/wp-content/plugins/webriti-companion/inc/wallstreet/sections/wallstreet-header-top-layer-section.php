<?php
if ( ! defined( 'ABSPATH' ) ) exit; // Exit if accessed directly

/**
 * Header Top Layer section for the homepage.
 */
if (!function_exists('webriti_companion_wallstreet_heading_section')) :

    function webriti_companion_wallstreet_heading_section() {
	    $wallstreet_pro_options=wallstreet_theme_data_setup();
		$current_options = wp_parse_args(  get_option( 'wallstreet_pro_options', array() ), $wallstreet_pro_options ); ?>

   	 	<!--Header Top Layer Section-->
		<div class="header-top-area">
			<div class="container">
				<div class="row">
					<div class="col-sm-6">
						<?php if($current_options['header_social_media_enabled']==true) { ?>
							<ul class="head-contact-social">
								<?php if($current_options['social_media_twitter_link']!='') { ?>
									<li><a href="<?php echo esc_url( $current_options['social_media_twitter_link'] ); ?>"><i class="fa-brands fa-x-twitter"></i></a></li>
								<?php }
								if($current_options['social_media_facebook_link']!='') { ?>
									<li><a href="<?php echo esc_url( $current_options['social_media_facebook_link'] ); ?>"><i class="fa-brands fa-facebook-f"></i></a></li>
								<?php }
								if($current_options['social_media_linkedin_link']!='') { ?>
									<li><a href="<?php echo esc_url( $current_options['social_media_linkedin_link'] ); ?>"><i class="fa-brands fa-linkedin-in"></i></a></li>
								<?php }
								if($current_options['social_media_youtube_link']!='') { ?>
									<li><a href="<?php echo esc_url( $current_options['social_media_youtube_link'] ); ?>"><i class="fa-brands fa-youtube"></i></a></li>
								<?php }
								if($current_options['social_media_instagram_link']!='') { ?>
									<li><a href="<?php echo esc_url( $current_options['social_media_instagram_link'] ); ?>"><i class="fa-brands fa-instagram"></i></a></li>
								<?php } ?>
							</ul>
						<?php } ?>
					</div>
					<div class="col-sm-6">
						<?php if($current_options['contact_header_settings']==true) { ?>
							<ul class="head-contact-info">
								<?php if($current_options['contact_phone_number']!=''){ ?>
									<li><i class="fa fa-phone-square"></i><?php echo esc_html($current_options['contact_phone_number']); ?></li>
								<?php } ?>
								<?php if($current_options['contact_email']!=''){ ?>
									<li><i class="fa fa-envelope"></i><?php echo esc_html($current_options['contact_email']); ?></li>
								<?php } ?>
							</ul>
						<?php } ?>
					</div>
				</div>
			</div>
		</div>
	<?php }
endif;

if (function_exists('webriti_companion_wallstreet_heading_section')) {
    add_action('wallstreet_custom_header', 'webriti_companion_wallstreet_heading_section');
}

//Set for old user
if (!get_option('wallstreet_user', false)) {
	//detect old user and set value
    $bluestreet_user = get_option('wallstreet_pro_options', array());
    if ((isset($bluestreet_user['service_title']) || isset($bluestreet_user['service_description']) || isset($bluestreet_user['home_blog_heading']) || isset($bluestreet_user['home_blog_description']))) {
        add_option('wallstreet_user', 'old');
    }
    else {
	    if ( version_compare(wp_get_theme()->get('Version'), '1.8.8') <= 0 ) {
	        add_option('wallstreet_user', 'new');
	    }
	    else {
	        add_option('wallstreet_user', 'new_user');
	    }
    }
}
