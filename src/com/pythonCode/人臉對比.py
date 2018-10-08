


from aip import AipFace
import base64

config_face = {
    'appId': '11838371',
    'apiKey': 'lpCc6lPDbdbqI8aDOKUS8GUo',
    'secretKey': '90kmZfsZDDYeCvfuiIKm5xP9fUVymLBU'
}

client_FACE = AipFace(**config_face)



IMAGE_TYPE='BASE64'

f1 = open('/Users/olivia/Desktop/WechatIMG71.jpeg','rb')
f2 = open('/Users/olivia/Desktop/WechatIMG70.jpeg','rb')
#参数 image：图像 base64 编码 分别 base64 编码后的 2 张图片数据
img1 = base64.b64encode(f1.read())
img2 = base64.b64encode(f2.read())
image_1 = str(img1,'utf-8')
image_2 = str(img2,'utf-8')



ptr = client_FACE.match([
    {
        'image': image_1,
        'image_type': 'BASE64',
    },
    {
        'image': image_2,
        'image_type': 'BASE64',
    }
])



ptr = ptr['result']
print(ptr['score']) #分數

