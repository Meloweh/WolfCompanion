from PIL import Image
import numpy as np
import sys

def compute_brightness(color):
    r, g, b = color[:3]
    brightness = 0.2126 * r + 0.7152 * g + 0.0722 * b
    return brightness

def get_unique_colors(image):
    image = image.convert('RGBA')  # Handle images with transparency
    pixels = np.array(image)
    pixels_flat = pixels.reshape(-1, 4)  # Flatten to a 2D array of RGBA values
    colors, counts = np.unique(pixels_flat, axis=0, return_counts=True)
    colors = colors.astype(np.uint8)
    return colors, counts

def colors_to_values(colors):
    # Convert RGBA colors to unique 32-bit integers
    colors_values = (colors[:, 0].astype(np.uint32) << 24) | \
                    (colors[:, 1].astype(np.uint32) << 16) | \
                    (colors[:, 2].astype(np.uint32) << 8)  | \
                    (colors[:, 3].astype(np.uint32))
    return colors_values

def main():
    if len(sys.argv) < 3:
        print("Usage: python color_transfer.py <image_a.png> <image_b.png>")
        sys.exit(1)
    image_a_path = sys.argv[1]
    image_b_path = sys.argv[2]

    # Load images
    image_a = Image.open(image_a_path)
    image_b = Image.open(image_b_path)

    # Get unique colors
    colors_a, counts_a = get_unique_colors(image_a)
    colors_b, counts_b = get_unique_colors(image_b)

    num_colors_a = len(colors_a)
    num_colors_b = len(colors_b)

    if num_colors_a < num_colors_b:
        raise ValueError(f"Image A has {num_colors_a} unique colors, but Image B has {num_colors_b}. Cannot proceed.")

    # Compute brightness
    brightness_a = np.array([compute_brightness(c) for c in colors_a])
    brightness_b = np.array([compute_brightness(c) for c in colors_b])

    # Sort colors based on brightness
    indices_a = np.argsort(brightness_a)
    indices_b = np.argsort(brightness_b)

    sorted_colors_a = colors_a[indices_a]
    sorted_colors_b = colors_b[indices_b]

    # Convert colors to 32-bit values
    colors_a_values = colors_to_values(sorted_colors_a)
    colors_b_values = colors_to_values(sorted_colors_b)

    # Create mapping from colors in B to colors in A
    mapping = dict(zip(colors_b_values, colors_a_values))

    # Process image B
    pixels_b = np.array(image_b.convert('RGBA'))
    pixels_b_flat = pixels_b.reshape(-1, 4)
    pixels_b_values = colors_to_values(pixels_b_flat)

    # Map the colors
    mapped_values = np.array([mapping[val] for val in pixels_b_values], dtype=np.uint32)

    # Convert values back to RGBA colors
    new_pixels_flat = np.zeros((len(mapped_values), 4), dtype=np.uint8)
    new_pixels_flat[:, 0] = (mapped_values >> 24) & 0xFF
    new_pixels_flat[:, 1] = (mapped_values >> 16) & 0xFF
    new_pixels_flat[:, 2] = (mapped_values >> 8) & 0xFF
    new_pixels_flat[:, 3] = mapped_values & 0xFF

    # Reshape to the original image shape
    new_pixels = new_pixels_flat.reshape(pixels_b.shape)

    # Create new image
    new_image = Image.fromarray(new_pixels, mode='RGBA')
    new_image.save('output_image.png')
    print("Output saved as 'output_image.png'")

if __name__ == '__main__':
    main()
