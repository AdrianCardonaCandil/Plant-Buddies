"""
Fichero que ejecuta inferencia mediante un modelo preentrenado.
------------------------------------------------------------------
Entrenamiento realizado gracias a un dataset llamado PlantNet300k.
Se realiza inferencia sobre una totalidad de 50 clases mediante un
conjunto de pesos que garantiza aproximadamente un 94.5% de validez
en las estimaciones.
"""

# Importación de librerías
import torch
from torchvision import transforms
import os
import sys
import json
from PIL import Image
from model import ResNet34

# Seccion de hiperparámetros
CHECKPOINT_PATH = './saved_models/plantnet_300k_resnet34_checkpoint.pth' # Ruta del archivo de checkpoint
IMAGE_SIZE = 224                                                         # Tamaño de la imagen de entrada (224x224)
NORMALIZE_MEAN = [0.4399, 0.4692, 0.3228]                                # Media de los valores de los canales RGB en el dataset de entrenamiento
NORMALIZE_STD = [0.2337, 0.2185, 0.2297]                                 # Desviación estándar de los valores de los canales RGB en el dataset de entrenamiento

# Configuración del entorno de ejecución
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

# Definition of checkpoint load function
def load_checkpoint(path):
    """
    Loads the state of the model, optimizer and scheduler from a file.

    Parameters
    -------------------------------------------------------------
    path (str): path to the file where the model is going to be loaded.
    """
    
    checkpoint = torch.load(path)
    return checkpoint

def load_model(model):
    """
    Loads the model from a checkpoint.

    Parameters
    -------------------------------------------------------------
    checkpoint (dict): dictionary containing the model's state.
    """
    
    if (os.path.exists(CHECKPOINT_PATH)):
        checkpoint = load_checkpoint(CHECKPOINT_PATH)
        model.load_state_dict(checkpoint['model'])
        model.to(device)
        model.eval()

def predict(model, image):
    """
    Predicts the class of an image.

    Parameters
    -------------------------------------------------------------
    model (nn.Module): model used to predict the image class.
    image (multipart): image to predict.
    """

    image = Image.open(image)

    # Preprocessing the image through various transformations
    transform = transforms.Compose([
        transforms.Resize((IMAGE_SIZE, IMAGE_SIZE), antialias = True),
        transforms.ToTensor(),
        transforms.Normalize(mean = NORMALIZE_MEAN, std = NORMALIZE_STD)
    ])

    image = transform(image).unsqueeze(0)
    image = image.to(device)

    # Performing prediction
    with torch.no_grad():
        output = model(image)
        prediction = torch.argmax(output, dim = 1).item()

    return prediction

if __name__ == '__main__':
    # Load the model
    model = ResNet34()
    load_model(model)

    # Predict the class of an image
    image = sys.argv[1]
    prediction = predict(model, image)
    print(json.dumps({'class': prediction}))