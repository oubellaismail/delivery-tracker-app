# VPC (mandatory)
resource "digitalocean_vpc" "vpc" {
  name   = "devsecops-vpc"
  region = var.region
}

# Firewall – no wide-open SSH; only 80/443 public, app_port internal
resource "digitalocean_firewall" "fw" {
  name = "devsecops-fw"

  # SSH from approved CIDRs only
  dynamic "inbound_rule" {
    for_each = var.allowed_ssh_cidrs
    content {
      protocol         = "tcp"
      port_range       = "22"
      source_addresses = [inbound_rule.value]
    }
  }

  # Public HTTP/HTTPS
  inbound_rule {
    protocol         = "tcp"
    port_range       = "80"
    source_addresses = ["0.0.0.0/0", "::/0"]
  }
  inbound_rule {
    protocol         = "tcp"
    port_range       = "443"
    source_addresses = ["0.0.0.0/0", "::/0"]
  }

  # App port allowed only inside VPC
  inbound_rule {
    protocol         = "tcp"
    port_range       = tostring(var.app_port)
    source_addresses = [digitalocean_vpc.vpc.ip_range]
  }

  # Egress (tighten later if needed)
  outbound_rule {
    protocol              = "tcp"
    port_range            = "all"
    destination_addresses = ["0.0.0.0/0", "::/0"]
  }
  outbound_rule {
    protocol              = "udp"
    port_range            = "all"
    destination_addresses = ["0.0.0.0/0", "::/0"]
  }
}

# Cloud-init for baseline hardening + Docker install
locals {
  cloud_init = <<-EOF
  #cloud-config
  package_update: true
  package_upgrade: true
  packages:
    - docker.io
    - fail2ban
    - unattended-upgrades
  runcmd:
    - systemctl enable --now docker
    - sed -i 's/^#PermitRootLogin .*/PermitRootLogin no/' /etc/ssh/sshd_config
    - sed -i 's/^#PasswordAuthentication .*/PasswordAuthentication no/' /etc/ssh/sshd_config
    - systemctl restart ssh
    - ufw --force enable
    - ufw allow 80/tcp
    - ufw allow 443/tcp
  EOF
}

# Staging droplet
resource "digitalocean_droplet" "staging" {
  name       = "staging-backend"
  region     = var.region
  size       = var.staging_size
  image      = "ubuntu-22-04-x64"
  vpc_uuid   = digitalocean_vpc.vpc.id
  ssh_keys   = var.ssh_key_ids
  user_data  = local.cloud_init
  monitoring = true
  tags       = ["env:staging", "app:backend"]
}

# Production droplet
resource "digitalocean_droplet" "production" {
  name       = "production-backend"
  region     = var.region
  size       = var.production_size
  image      = "ubuntu-22-04-x64"
  vpc_uuid   = digitalocean_vpc.vpc.id
  ssh_keys   = var.ssh_key_ids
  user_data  = local.cloud_init
  monitoring = true
  tags       = ["env:production", "app:backend"]
}

# New - Dedicated Staging Database Cluster
resource "digitalocean_database_cluster" "staging_db" {
  count     = var.enable_managed_db ? 1 : 0
  name      = "staging-db"
  engine    = var.db_engine
  version   = var.db_version
  size      = "db-s-1vcpu-1gb"
  region    = var.region
  node_count = 1
  private_network_uuid = digitalocean_vpc.vpc.id
  tags = ["env:staging", "app:backend"]
}

# Dedicated Production Database Cluster
resource "digitalocean_database_cluster" "production_db" {
  count     = var.enable_managed_db ? 1 : 0
  name      = "production-db"
  engine    = var.db_engine
  version   = var.db_version
  size      = "db-s-1vcpu-1gb"
  region    = var.region
  node_count = 1
  private_network_uuid = digitalocean_vpc.vpc.id
  maintenance_window {
    day  = "monday"
    hour = "03:00"
  }
  tags = ["env:production", "app:backend"]
}

# New - Dedicated Databases and Users for each cluster
resource "digitalocean_database_db" "staging_db_name" {
  count      = var.enable_managed_db ? 1 : 0
  cluster_id = digitalocean_database_cluster.staging_db[0].id
  name       = var.db_name
}

resource "digitalocean_database_db" "production_db_name" {
  count      = var.enable_managed_db ? 1 : 0
  cluster_id = digitalocean_database_cluster.production_db[0].id
  name       = var.db_name
}

# You’ll inject DB creds + JWT via CI/CD (never here).
