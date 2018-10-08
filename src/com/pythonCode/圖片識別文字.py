

from aip import AipOcr

config_OCR = {
    'appId': '11829853',
    'apiKey': '7BZ8kGPrAB4MNzKikvVf5AhT',
    'secretKey': 'E84f9ON66f2Dr1MVvxlcNObu3VynOX7o'
}

client_OCR = AipOcr(**config_OCR)



def get_file_content(file):
    with open(file, 'rb') as fp:
        return fp.read()

def img_to_str(image_path):
    image = get_file_content(image_path)
    result = client_OCR.basicGeneral(image)
    if 'words_result' in result:
        return ''.join([w['words'] for w in result['words_result']])



string = str(img_to_str("/Users/olivia/Desktop/sample1.png")) #導入照片
string #輸出字串

