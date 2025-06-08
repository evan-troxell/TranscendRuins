
const { registerBlockType } = wp.blocks;
const el = wp.element.createElement;
const { TextControl } = wp.components;

const documentationPath = '/wp-content/uploads/documentation';

const createIFrame = (attributes) => el('iframe', {
    className: 'documentation-iframe',
    src: attributes.url,
  });

registerBlockType('custom/documentation-iframe', {
  title: 'Documentation Iframe',
  icon: 'media-document',
  category: 'embed',
  attributes: {
    categoryInput: { type: 'string', default: 'Input documentation category' },
    url: { type: 'string', default: '' },
  },
  edit({ attributes, setAttributes }) {

    return el(
        'div',
        {},
        el(TextControl, {
        label: 'Documentation Category',
        value: attributes.categoryInput,
        onChange: async (category) => {
          setAttributes({ categoryInput: category });
          const documentationFile = await fetch('/wp-content/uploads/documentation/documentation.json');
          const documentationJSON = await documentationFile.json();

          const categoryJSON  = documentationJSON[category];
          if (!categoryJSON) return;

          const versions = categoryJSON.versions
          const latestVersion = categoryJSON.latest || getLatestVersion(versions);

          if (!latestVersion) return;

          const url = documentationPath + '/' + latestVersion + '/' + category + '.html';
          setAttributes({ url })
        },
        }),
        createIFrame(attributes)
    );
  },
  save({ attributes }) {
    return createIFrame(attributes);
  }
});