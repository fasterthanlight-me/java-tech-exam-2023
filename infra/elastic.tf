resource "aws_security_group" "elastic-sg" {
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 9200
    to_port     = 9200
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "elastic-ec2" {
  ami                    = "ami-0669b163befffbdfc"
  instance_type          = "t2.medium"
  key_name               = aws_key_pair.default.key_name
  vpc_security_group_ids = [aws_security_group.elastic-sg.id]
  user_data_base64       = filebase64("${path.module}/elastic-user-data.sh")
}

output "elastic-ip" {
  value = aws_instance.elastic-ec2.public_ip
}

