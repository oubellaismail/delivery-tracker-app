# Create VPC
resource "digitalocean_vpc" "project_vpc" {
  name   = "devsecops-vpc"
  region = var.region
}

# Firewall
resource "digitalocean_firewall" "fw" {
  name = "devsecops-fw"

  inbound_rule {
    protocol         = "tcp"
    port_range       = "22"
    source_addresses = ["0.0.0.0/0"]
  }

  inbound_rule {
    protocol         = "tcp"
    port_range       = "80"
    source_addresses = ["0.0.0.0/0"]
  }

  inbound_rule {
    protocol         = "tcp"
    port_range       = "443"
    source_addresses = ["0.0.0.0/0"]
  }

  outbound_rule {
    protocol              = "tcp"
    port_range            = "all"
    destination_addresses = ["0.0.0.0/0"]
  }
}

# Staging Droplet
resource "digitalocean_droplet" "staging" {
  name     = "staging-backend"
  region   = var.region
  size     = var.size
  image    = var.image
  vpc_uuid = digitalocean_vpc.project_vpc.id
  ssh_keys = [var.ssh_key_id]
}

# Production Droplet
resource "digitalocean_droplet" "production" {
  name     = "production-backend"
  region   = var.region
  size     = var.size
  image    = var.image
  vpc_uuid = digitalocean_vpc.project_vpc.id
  ssh_keys = [var.ssh_key_id]
}
