"""
Fichero que contiene la arquitectura de la red neuronal ResNet 34.
------------------------------------------------------------------
Red neuronal ResNet34 con un clasificador personalizado. El modelo
ha sido preentrenado con el dataset PlantNet300k. Clasificación de
un total de 50 clases. El porcentaje de acierto tras finalizar las
épocas de entrenamiento es de un 94.5%.
"""

import torch.nn as nn
from torchvision.models import ResNet34_Weights, resnet34

# Hiperparámetros de entrenamiento
CLASS_FILTER_AMOUNT = 50           # Número de clases a clasificar
NON_DOWNSAMPLING_KERNEL_SIZE = 3   # Tamaño del kernel para convoluciones sin downsampling
NON_DOWNSAMPLING_STRIDE = 1        # Stride para convoluciones sin downsampling
PADDING_SIZE = 1                   # Padding para convoluciones
DROPOUT_RATE_FC1 = 0.4             # Tasa de dropout para la primera capa totalmente conectada.

# Definition of a restnet34 model
class ResNet34(nn.Module):
    def __init__(self):
        super().__init__()
        """
        ResNet34 model with a custom classifier. The model is pretrained with the ImageNet dataset.
        First convolutional layer is described with 3 input channels, 64 output channels, kernel size of 3, stride of 1 and padding of 1.
        First fully connected layer is described with ResNet34's number of features as input, 512 output features.
        Second fully connected layer is described with 512 input features and number of classes as output features.
        """
        self.resnet34 = resnet34(weights = ResNet34_Weights.IMAGENET1K_V1)
        num_features = self.resnet34.fc.in_features
        self.resnet34.conv1 = nn.Conv2d(3, 64, kernel_size = NON_DOWNSAMPLING_KERNEL_SIZE, stride = NON_DOWNSAMPLING_STRIDE, padding = PADDING_SIZE, bias = False)
        self.resnet34.fc = nn.Sequential(
            nn.Linear(num_features, 512),
            nn.ReLU(),
            nn.Dropout(DROPOUT_RATE_FC1),
            nn.Linear(512, CLASS_FILTER_AMOUNT)
        )

    def forward(self, x):
        return self.resnet34(x)