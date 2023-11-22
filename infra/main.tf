terraform {
  required_providers {
    aws = {
      version = "4.2.0"
    }
  }
  backend "s3" {
    bucket         = "ftl-nazar-terraform-state"
    key            = "terraform.tfstate"
    region         = "eu-central-1"
  }
}

provider "aws" {
  region = var.region
}
