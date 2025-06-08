<?php 
/**
 * Template Name: Blog
 * Description: A template for displaying a blog with a custom post type.
 */
?>

<?php get_header(); ?>

<h1 class="title"><?php the_title(); ?></h1>

<div id="blog-tags-menu-container">

    <div id="blog-tags-selected-container" class="container">
        <ul id="blog-tags-selected" class="blog-tags-container"></ul>
        <div id="blog-tags-selected-control-container" class="container">
            <button id="blog-tags-selector-expander-button">Select Tags<span class="dropdown-arrow">▾</span></button>
            <button id="blog-tags-clear-button">Clear Selection</button>
        </div>
    </div>

    <div id="blog-tag-selector-container" class="container">
        <div id="blog-tag-search-container" class="container">
            <input type="text" id="blog-tag-search" placeholder="Search tags..." /></input>
        </div>
        <ul id="blog-tags-unselected" class="blog-tags-container"></ul>
    </div>
</div>

<div id="blog-posts-container" class="container">
    <?php
        $page_id = get_the_ID();
        $post_type = get_post_meta($page_id, 'display_post_type', true);

        if (!$post_type) {
            $post_type = 'post';
        }
        $args = array(
            'category_name' => $post_type,
            'posts_per_page' => -1,
            'orderby' => 'date',
            'order' => 'DESC'
        );
        
        $query = new WP_Query($args);

        if ($query->have_posts()) {

            echo '<ul id="blog-posts">';

            while ($query->have_posts()) {

                $query->the_post();

                $tags = get_the_tags();
                
                echo '<li class="blog-post"';

                if ($tags) {
                    echo ' data-tags="';
                    foreach ($tags as $tag) {
                        echo $tag->slug . ' ';
                    }
                    echo '"';
                }
                
                echo '>';

                echo '<div class="blog-post-metadata-container">';

                $author_id = get_the_author_meta('ID');
                $author_page_href = get_author_posts_url($author_id);
                echo '<a class="blog-author-avatar" href="' . $author_page_href . '">' . get_avatar($author_id, 40) . '</a>';

                echo '<div class="blog-post-publication-container">';
                echo '<a class="blog-post-author-name" href="' . $author_page_href . '">' . get_the_author() . '</a>';
                echo '<p class="blog-post-publication-date">' . get_the_date() . '</p>';
                echo '</div>';

                echo '</div>';

                echo '<div class="blog-post-content-container">';
                echo '<a href="' . get_permalink() . '"><h3 class="blog-post-title">' . get_the_title() . '</h3></a>';
                echo '<p class="blog-post-excerpt">' . get_the_excerpt() . '</p>';
                
                if ($tags) {
                    echo '<ul class="blog-post-tags blog-tags-container">';
                    foreach ($tags as $tag) {
                        echo '<li class="blog-tag" data-slug="' . esc_html($tag->slug) . '"><span style="color: #00F;">#</span>' . esc_html($tag->name) . '</li>';
                    }
                    echo '</ul>';
                }

                echo '</li>';
            }
            echo '</ul>';
        } else {

            echo '<p>No posts found, check back later!</p>';
        }
    ?>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {

        const postsContainer = document.getElementById('blog-posts-container');
        const posts = postsContainer.querySelectorAll('.blog-post');

        const tagSelectorExpanderButton = document.getElementById('blog-tags-selector-expander-button');
        const tagSelectorContainer = document.getElementById('blog-tag-selector-container');
        const tagClearButton = document.getElementById('blog-tags-clear-button');

        const tagSearchContainer = document.getElementById('blog-tag-search-container');
        const tagSearchInput = document.getElementById('blog-tag-search');
        const selectedTagsContainer = document.getElementById('blog-tags-selected');
        const unselectedTagsContainer = document.getElementById('blog-tags-unselected');

        const tags = {};

        for (let blogTag of document.querySelectorAll('.blog-tag')) {

            const slug = blogTag.dataset.slug;

            if (!tags[slug]) {

                const tag = document.createElement('li');

                tag.innerHTML = blogTag.innerHTML;
                tag.classList.add('blog-tag');

                tag.dataset.slug = slug;
                tag.dataset.count = 0;

                tag.addEventListener('click', event => {

                    event.stopPropagation();
                    selectTag(tag);
                });

                tags[slug] = tag;
                unselectedTagsContainer.appendChild(tag);
            }

            const tag = tags[slug];
            tag.dataset.count++;
            
            blogTag.addEventListener('click', event => {

                event.stopPropagation();
                selectTag(tag);
            });
        }

        unselectedTagsContainer.querySelectorAll('.blog-tag').forEach(tag => {

            tags[tag.dataset.slug] = tag;
        });

        sortUnselected();
        let selectedTags = [];

        tagSelectorExpanderButton.addEventListener('click', () => {

            const isExpanded = tagSelectorExpanderButton.classList.toggle('expanded');
            tagSelectorContainer.classList.toggle('display');
            tagSearchContainer.classList.toggle('display');
        });

        tagClearButton.addEventListener('click', () => {

            for (let tag of selectedTags) {

                tag.classList.remove('selected');
                unselectedTagsContainer.appendChild(tag);
            }
            selectedTags = [];

            filterPosts();
            sortUnselected();
        });

        function selectTag(tag) {

            tag.classList.toggle('selected');

            if (tag.classList.contains('selected')) {

                selectedTagsContainer.appendChild(tag);
                selectedTags.push(tag);
            } else {

                unselectedTagsContainer.appendChild(tag);

                const index = selectedTags.indexOf(tag);
                if (index > -1) {

                    selectedTags.splice(index, 1);
                }

                sortUnselected();
            }

            filterPosts();
        }

        function filterPosts() {

            posts.forEach(post => {
                const postTags = post.dataset.tags ? post.dataset.tags.split(' ') : [];

                const matches = selectedTags.length === 0 || 
                                selectedTags.some(tag => postTags.includes(tag.dataset.slug));

                post.classList.toggle('hidden', !matches);
            });
        }

        function sortUnselected() {

            const tags = Array.from(unselectedTagsContainer.querySelectorAll('.blog-tag'));

            tags.sort((a, b) => {
                const aCount = Number(a.dataset.count) || 0;
                const bCount = Number(b.dataset.count) || 0;
                return bCount - aCount;
            });

            tags.forEach(tag => unselectedTagsContainer.appendChild(tag));
        }

        function filterTag(tag) {
            
            const searchTerm = tagSearchInput.value.toLowerCase();

            if (!searchTerm || tag.textContent.toLowerCase().includes(searchTerm)) {

                tag.classList.remove("hidden");
            } else {

                tag.classList.add("hidden");
            }
        }

        tagSearchInput.addEventListener('input', event => {

            const selected = unselectedTagsContainer.querySelectorAll('.blog-tag');
            selected.forEach(filterTag);
        });

        tagSearchInput.addEventListener('keydown', event => {

            if (event.key === 'Enter') {

                const selected = unselectedTagsContainer.querySelectorAll('.blog-tag:not(.hidden)');
                if (selected[0]) {

                    selectTag(selected[0]);
                    tagSearchInput.value = '';

                    const unselected = unselectedTagsContainer.querySelectorAll('.blog-tag');
                    unselected.forEach(filterTag);
                }
            }
        });
    });
</script>

<?php
	wp_reset_postdata();
	the_content();
?>

<?php get_footer(); ?>